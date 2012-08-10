package be.humanoids.ma;

import java.awt.image.BufferedImage;
/**
 * Creates a simple visualizer out of a Tone array.
 * @author Martin
 */
public class Visualizer {
    Tone[] freq;
    BufferedImage img;
    
    Visualizer(Tone[] frequencies) {
        freq = frequencies;
        img = new BufferedImage(freq.length,250,BufferedImage.TYPE_INT_RGB);
    }
    
    public BufferedImage createImage() {
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
        
        for(int f=0;f<freq.length;f++) {
            int height = (int)Math.floor(freq[f].getAplitude()*factor);
            for(int y = 1;y<=height;y++) {
                img.setRGB(f+1, y, col);
            }
        }
        
        return img;
    }
    
    public void clearImage() {
        int r = 255;
        int g = 255;
        int b = 255;
        int col = (r << 16) | (g << 8) | b; // gives white
        for(int x=1;x<freq.length;x++) {
            for(int y=1;y<250;y++) {
                img.setRGB(x,y,col);
            }
        }
    }
}
