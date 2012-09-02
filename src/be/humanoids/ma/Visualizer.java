package be.humanoids.ma;

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
    private List _listeners = new ArrayList();
    public synchronized void addEventListener(VisualizerEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(VisualizerEventListener listener) {
        _listeners.remove(listener);
    }

    private synchronized void fireEvent() {
        VisualizerEvent event = new VisualizerEvent(this,img);
        Iterator i = _listeners.iterator();
        while(i.hasNext()) {
            ((VisualizerEventListener) i.next()).handleVisualizerEvent(event);
        }
    }
    
    
    private Tone[] freq;
    private float[] data;
    BufferedImage img;
    private boolean type; // false=waveform, true=fft
    private boolean ready;
    static String WAVEFORM = "Waveform";
    static String FFT = "Transform";
    
    Visualizer(int width, int height) {

        ready = false;
        img = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
    }
    
    private BufferedImage createTransformImage() {
        clearImage();
        int r = 0;
        int g = 255;
        int b = 0;
        int col = (r << 16) | (g << 8) | b; // green
        
        // calculate required compression on x-axis
        int compression = (int)Math.ceil(img.getWidth()/freq.length);
        
        // make sure it gets scaled, so everything fits into the graphicsfield (y-axis)
        float maxAmp = 0;
        for(int f=0;f<freq.length;f++) {
            if(freq[f].getAmplitude()>maxAmp)
                maxAmp = freq[f].getAmplitude();
        }
        double factor = maxAmp/250;
        
        for(int f=0;f<img.getWidth();f++) {
            int fheight = 0;
            for(int j=0;j<compression;j++) {
                fheight = (int) (fheight + Math.floor(freq[f*compression+j].getAmplitude()/factor));
            }
            fheight = fheight/compression;
            for(int y = 1;y<=fheight;y++) {
                img.setRGB(f, img.getHeight()-y, col);
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
        int compression = (int)Math.ceil(img.getWidth()/data.length);
        
        for(int i=0;i<img.getWidth();i++) {
            int fheight = 0;
            for(int j=0;j<compression;j++) {
                fheight = (int)(fheight + Math.floor(freq[i*compression+j].getAmplitude()));
            }
            fheight = fheight/compression;
            int wheight = (int)(Math.floor(fheight+128)/1.024);
            img.setRGB(i,img.getHeight()-wheight-1 ,col);
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
        if(!ready)
            ready = true;
        if(type)
            createImage();
    }
    
    public void updateData(float[] a) {
        data = a;
        if(!ready)
            ready = true;
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
    
    public void toggleType() {
        type = !type;
    }
    
    public String getType() {
        if(type)
            return Visualizer.FFT;
        else
            return Visualizer.WAVEFORM;
    }
}
