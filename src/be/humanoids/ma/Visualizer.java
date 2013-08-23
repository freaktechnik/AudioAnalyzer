package be.humanoids.ma;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
/**
 * Creates a simple visualizer out of a Tone array.
 * @author Martin
 */
public class Visualizer extends JPanel {
   
    private Tone[] freq;
    private float[] data;
    private BufferedImage img;
    private Type type;
    private boolean ready;
    private boolean imgready;
    
    public static enum Type {
        EQUALIZER,
        WAVEFORM
    }
    
    Visualizer() {
        super();
        ready = false;
        imgready = true;
        
        this.type = Type.WAVEFORM;
        this.setPreferredSize( new Dimension(300, 200) ) ;
        
    }
    
    Visualizer(Type type) {
        super();
        ready = false;
        imgready = true;
        
        this.type = type;
        this.setPreferredSize( new Dimension(300, 200) ) ;
    }
    
    private BufferedImage createTransformImage() {
        clearImage();
        int r = 0;
        int g = 255;
        int b = 0;
        int col = (r << 16) | (g << 8) | b; // green
        
        // calculate required compression on x-axis
        int compression = 1;
        if(freq.length>2*img.getWidth()) {
            compression = (int)Math.floor(freq.length/img.getWidth());
        }
        
        // make sure it gets scaled, so everything fits into the graphicsfield (y-axis)
        double maxAmp = 0;
        for(int f=0;f<freq.length;f++) {
            if(freq[f].getAmplitude()>maxAmp) {
                maxAmp = freq[f].getAmplitude();
            }
        }
        // Logarithmic compression here
        double factor = img.getHeight()/maxAmp;
        
        for(int f=0;f<img.getWidth();f++) {
            double maxAmph = 0;
            for(int i = f*compression;f<(f+1)*compression;f++) {
                if(freq[i].getAmplitude()>maxAmph)
                    maxAmph=freq[i].getAmplitude();
            }
            int fheight= (int) (maxAmph/factor);
            for(int y = 1;y<fheight;y++) {
                img.setRGB(f, Math.abs(img.getHeight()-y), col);
            }
        }
        
        return img;
    }
    
    private BufferedImage createWaveformImage() {
        clearImage();
        int r = 0;
        int g = 0;
        int b = 255;
        int col = (r << 16) | (g << 8) | b; // blue
        
        // calculate required compression on x-axis
        int compression = 1;
        if(data.length>2*img.getWidth()) {
            compression = (int)Math.floor(data.length/img.getWidth());
        }
        
        double factor = img.getHeight()/256;
        
        int fheight, wheight;
        
        for(int i=0;i<img.getWidth();i++) {
            try {
                fheight = 0;
                for(int j=0;j<compression;j++) {
                    fheight = (int)(fheight + Math.floor(data[i*compression+j]));
                }
                fheight = fheight/compression;
                wheight = (int)(Math.floor((fheight+128)*factor));

                img.setRGB(i,Math.abs(img.getHeight()-wheight) ,col);
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }

        return img;
    }
    
    private void clearImage() {
        int r = 255;
        int g = 255;
        int b = 255;
        int col = (r << 16) | (g << 8) | b; // gives white
        for(int x=0;x<img.getWidth();x++) {
            for(int y=0;y<img.getHeight();y++) {
                img.setRGB(x,y,col);
            }
        }
    }
    
    public void updateFrequencies(Tone[] f) {
        freq = f;
        if(!ready&&imgready) {
            ready = true;
        }
    }
    
    public void updateData(float[] a) {
        data = a;
        if(!ready&&imgready) {
            ready = true;
        }
    }
    
    private BufferedImage createImage() {
        img = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
        if(ready) {
            if(type == Type.EQUALIZER)
                return this.createTransformImage();
            else
                return this.createWaveformImage();
        }
        return null;
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);
        
        createImage();
        //g2d.drawImage(img,0 ,-this.getHeight(), this);
        g2d.drawImage(img, null, 0, 0);
    }
    
    public void toggleType() {
        if( type == Type.EQUALIZER )
            type = Type.WAVEFORM;
        else
            type = Type.EQUALIZER;
    }
    
    public void setType(Type t) {
        this.type = t;
    }
    
    public Type getType() {
        return this.type;
    }
}
