/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

/**
 *
 * @author Martin
 */
public class HPS {
    private Tone[] data;
    private Tone hps;
    private int depth;
    
    HPS(Tone[] data,int depth) {
        this.data = data;
        this.depth = depth;
        this.calculateHPS();
    }
    
    private void calculateHPS() {
        Tone[] tempSpec;
        Tone tempData,tempShrink;
        double maxAmp = 0;
        int maxI = 0;
        for(int i=1;i<depth-1;++i) {
            tempSpec = shrinkSpectrum(data,i+1);
            for(int j=0;j<tempSpec.length;++j) {
                tempShrink = tempSpec[j];
                tempData = data[j];
                data[j] = new Tone(tempData.getFrequency()*tempShrink.getFrequency(),tempData.getAmplitude()*tempShrink.getAmplitude());
            }
        }
        
        for(int k=0;k<data.length/depth;++k) {
            if(maxAmp<data[k].getAmplitude()) {
                maxAmp = data[k].getAmplitude();
                maxI = k;
            }
        }
        
        hps = data[maxI];
    }
    
    private Tone[] shrinkSpectrum(Tone[] input,int factor) {
        int goalLength = input.length/factor;
        Tone[] shrinkedSpectrum = new Tone[goalLength];
        
        for(int i=0;i<goalLength;++i) {
            shrinkedSpectrum[i] = getInterpolatedTone(input,i*factor,factor);
        }
        return shrinkedSpectrum;
    }
    
    private Tone getInterpolatedTone(Tone[] input,int start, int length) {
        float freq = 0;
        float amp = 0;
        for(int i=start;i<length;++i) {
            freq += input[i].getFrequency();
            amp += input[i].getAmplitude();
        }
        
        return new Tone(freq/length,amp/length);
    }
    
    public float getFrequency() {
        return hps.getFrequency();
    }
    
    public Tone getTone() {
        return hps;
    }
}
