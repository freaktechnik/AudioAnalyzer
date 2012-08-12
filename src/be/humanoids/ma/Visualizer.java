package be.humanoids.ma;

import java.awt.image.BufferedImage;
/**
 * Creates a simple visualizer out of a Tone array.
 * @author Martin
 */
public class Visualizer {
    Tone[] freq;
    double[] data;
    BufferedImage img;
    
    Visualizer(Tone[] frequencies) {
        freq = frequencies;
        img = new BufferedImage(freq.length/50,250,BufferedImage.TYPE_INT_RGB);
    }
    
    Visualizer(double[] by) {
        data = by;
        img = new BufferedImage(data.length,250,BufferedImage.TYPE_INT_RGB);
    }
    
    public BufferedImage createTransformImage() {
        if(img.getWidth()!=freq.length/50)
            img = new BufferedImage(freq.length/50,250,BufferedImage.TYPE_INT_RGB);
        clearImage();
        int r = 0;
        int g = 255;
        int b = 0;
        int col = (r << 16) | (g << 8) | b; // green
        
        // make sure it gets scaled, so everything fits into the graphicsfield
        double maxAmp = 0;
        for(int f=0;f<freq.length;f++) {
            if(freq[f].getAplitude()>maxAmp)
                maxAmp = freq[f].getAplitude();
        }
        double factor = maxAmp/250;
        
        for(int f=0;f<freq.length/50;f++) {
            int height = 0;
            for(int j=0;j<50;j++) {
                height = (int) (height + Math.floor(freq[f*50+j].getAplitude()/factor));
            }
            height = height/50;
            for(int y = 1;y<=height;y++) {
                img.setRGB(f, 250-y, col);
            }
        }
        
        return img;
    }
    
    public BufferedImage createWaveformImage() {
        if(img.getWidth()!=data.length)
            img = new BufferedImage(data.length,250,BufferedImage.TYPE_INT_RGB);
        clearImage();
        int r = 0;
        int g = 0;
        int b = 255;
        int col = (r << 16) | (g << 8) | b; // blue
        
        for(int i=0;i<data.length;i++) {
            int height = (int)(Math.floor(data[i]+128)/1.024);
            img.setRGB(i,250-height-1 ,col);
        }
        
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
    }
    
    public void updateData(double[] a) {
        data = a;
    }
}
