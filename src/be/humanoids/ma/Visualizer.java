package be.humanoids.ma;

import java.awt.Color;
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
    
    private double eqfactor;
    
    public static enum Type {
        EQUALIZER,
        WAVEFORM
    }
    
    Visualizer() {
        super();
        ready = false;
        imgready = true;
        eqfactor = 1;
        
        img = new BufferedImage(400,200,BufferedImage.TYPE_INT_RGB);
        clearImage();
        
        this.type = Type.WAVEFORM;
        this.setPreferredSize( new Dimension(400, 200) ) ;
        
    }
    
    Visualizer(Type type) {
        super();
        ready = false;
        imgready = true;
        eqfactor = 1;
        
        img = new BufferedImage(400,200,BufferedImage.TYPE_INT_RGB);
        clearImage();
        
        this.type = type;
        this.setPreferredSize( new Dimension(400, 200) ) ;
    }
    
    private BufferedImage createTransformImage() {
        clearImage();
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.green);
        
        // calculate required compression on x-axis
        int compression = 1;
        if(freq.length>2*img.getWidth()) {
            compression = (int)Math.floor(freq.length/img.getWidth());
        }
        
        // make sure it gets scaled, so everything fits into the graphicsfield (y-axis)
        double maxAmp = 0;
        for(int f=0;f<freq.length;f++) {
            if(freq[f].getAmplitudeIndB()>maxAmp)
            {
                maxAmp = freq[f].getAmplitudeIndB();
            }
        } 
        // Logarithmic compression here
        double factor = img.getHeight()/maxAmp;

        // die transformationsansicht bleibt länger klein und wird langsam grösser, past sich aber schnell an laute frequenzen an
        if( factor < eqfactor ) {
            eqfactor += ( factor - eqfactor ) / 2;
        }
        else {
            eqfactor += ( factor - eqfactor ) / 10;
        }
        
        for(int f=img.getMinX();f<img.getWidth();f++) {
            double maxAmph = 0;
            for(int i = f*compression;i<(f+1)*compression;i++) {
                if(freq[i].getAmplitudeIndB()>maxAmph)
                    maxAmph=freq[i].getAmplitudeIndB();
            }
            int fheight= (int) (maxAmph*eqfactor);
            g2d.drawLine(f,img.getHeight()-1,f,img.getHeight()-fheight);
        }
        
        return img;
    }
    
    private BufferedImage createWaveformImage() {
        clearImage();
        Graphics2D g2d = img.createGraphics();
        g2d.setColor(Color.yellow);
        
        // calculate required compression on x-axis
        int compression = 1;
        if(data.length>2*img.getWidth()) {
            compression = (int)Math.floor(data.length/img.getWidth());
        }
        
        double factor = img.getHeight() / 256.0;
        
        int fheight, wheight;
        int lastx = 0, lasty = img.getHeight() / 2;
        
        for(int i=img.getMinX();i<img.getWidth();i++) {
            try {
                fheight = 0;
                for(int j=0;j<compression;j++) {
                    fheight = (int)(fheight + Math.floor(data[i*compression+j]));
                }
                fheight = fheight/compression;
                wheight = (int)(Math.floor((fheight+128)*factor));

                g2d.drawLine(lastx, lasty, i, wheight);
                lastx = i;
                lasty = wheight;
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }
        return img;
    }
        
    private void clearImage() {
        img.getGraphics().clearRect(img.getMinX(), img.getMinY(), img.getWidth() - 1, img.getHeight() - 1);
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
