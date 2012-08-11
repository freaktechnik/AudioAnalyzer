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
        img = new BufferedImage(freq.length/50+1,251,BufferedImage.TYPE_INT_RGB);
    }
    
    Visualizer(double[] by) {
        data = by;
        img = new BufferedImage(data.length+1,251,BufferedImage.TYPE_INT_RGB);
    }
    
    public BufferedImage createTransformImage() {
        clearImage();
        int r = 0;
        int g = 255;
        int b = 0;
        int col = (r << 16) | (g << 8) | b;
        
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
                img.setRGB(f+1, 250-y, col);
            }
        }
        
        return img;
    }
    
    public BufferedImage createWaveformImage() {
        clearImage();
        int r = 0;
        int g = 0;
        int b = 255;
        int col = (r << 16) | (g << 8) | b;
        
        double max = 0;
        for(int i =0;i<data.length;i++) {
            data[i]+=128;
        }

        double factor = 256/250;
        
        for(int i=0;i<data.length;i++) {
            double actual = data[i];
            int height = (int)(Math.floor(data[i])/factor);
            img.setRGB(i+1,height ,col);
        }
        
        return img;
    }
    
    public void clearImage() {
        int r = 255;
        int g = 255;
        int b = 255;
        int col = (r << 16) | (g << 8) | b; // gives white
        for(int x=1;x<=freq.length/50;x++) {
            for(int y=1;y<=250;y++) {
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
