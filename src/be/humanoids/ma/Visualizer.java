package be.humanoids.ma;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * Creates a simple visualizer out of a Tone array.
 * @author Martin
 */
public class Visualizer {
    // Event stuff
    @SuppressWarnings("unchecked")
    private List<VisualizerEventListener> _listeners = new ArrayList();
    public synchronized void addEventListener(VisualizerEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(VisualizerEventListener listener) {
        _listeners.remove(listener);
    }

    private synchronized void fireEvent() {
        VisualizerEvent event = new VisualizerEvent(this);
        Iterator i = _listeners.iterator();
        while(i.hasNext()) {
            ((VisualizerEventListener) i.next()).handleVisualizerEvent(event);
        }
    }
    
    
    private Tone[] freq;
    private float[] data;
    private BufferedImage img;
    private boolean type; // false=waveform, true=fft
    private boolean ready;
    private boolean imgready;
    static final String WAVEFORM = "Waveform";
    static final String FFT = "Transform";
    
    Visualizer(int width, int height) {
        ready = false;
        imgready = true;
        img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
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
        int maxAmpi = 0;
        for(int f=0;f<freq.length;f++) {
            if(freq[f].getAmplitude()>maxAmp) {
                maxAmp = freq[f].getAmplitude();
                maxAmpi = f;
            }
        }
        System.out.println(freq[maxAmpi].getAbsoluteName());
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
        
        fireEvent();
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
        
        double factor = 256/img.getHeight();
        
        for(int i=0;i<img.getWidth();i++) {
            int fheight = 0;
            for(int j=0;j<compression;j++) {
                fheight = (int)(fheight + Math.floor(data[i*compression+j]));
            }
            fheight = fheight/compression;
            int wheight = (int)(Math.floor((fheight+128)*factor));
            img.setRGB(i,Math.abs(img.getHeight()-wheight) ,col);
        }
        
        fireEvent();
        return img;
    }
    
    public void clearImage() {
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
        if(type)
            createImage();
    }
    
    public void updateData(float[] a) {
        data = a;
        if(!ready&&imgready) {
            ready = true;
        }
        if(!type)
            createImage();
    }
    
    public BufferedImage createImage() {
        if(ready) {
            if(type)
                return this.createTransformImage();
            else
                return this.createWaveformImage();
        }
        return null;
    }
    
    public Image getImage() {
        return img;
    }
    
    public void toggleType() {
        type = !type;
    }
    
    public void setType(String t) {
        switch (t) {
            case Visualizer.FFT:
                type = true;
                break;
            case Visualizer.WAVEFORM:
                type = false;
                break;
        }
        
    }
    
    public String getType() {
        if(type)
            return Visualizer.FFT;
        else
            return Visualizer.WAVEFORM;
    }
}
