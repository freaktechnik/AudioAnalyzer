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
        this.data = new Tone[data.length];
        System.arraycopy(data, 0, this.data, 0, data.length);
        this.depth = depth;
        hps = this.calculateHPS();
    }
    
    private Tone calculateHPS() {
        Tone[] tempSpec;
        Tone tempData,tempShrink;
        double maxAmp = 0;
        int maxI = 0;
        for(int i=2;i<=depth;++i) {
            // always make the spectrum half as long
            tempSpec = shrinkSpectrum(data,i);
            for(int j=0;j<tempSpec.length;++j) {
                tempShrink = tempSpec[j];
                tempData = data[j];
                data[j] = new Tone(tempData.getFrequency()*tempShrink.getFrequency(),tempData.getAmplitude()*tempShrink.getAmplitude());
                
                if(i==depth&&maxAmp<data[j].getAmplitude()) {
                    maxAmp = data[j].getAmplitude();
                    maxI = j;
                }
            }
        }
        
        return data[maxI];
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
        for(int i=start;i<start+length;++i) {
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
