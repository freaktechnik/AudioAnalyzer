/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

/**
 * Used to get the correct Tone for tuning and stabilizing the output
 * @author Martin
 */
public class Filter {
    private Tone[] lastTones;
    private int toneBufferSize = 8;
    private int lastTone = 0;
    private Tone last;
    private boolean firstCycle;
    private int maxDelta = 20;
    
    Filter() {
        lastTones = new Tone[toneBufferSize];
        firstCycle = false;
    }
    
    /**
     * Adds a new tone from a frequency spectrum to the Buffer
     * @param newSet Spectrum the Tone gets extracted from
     */
    public void setNewTone(Tone[] newSet) {
        int maxI = 0;
        float max = 0;
        for(int i=1;i<newSet.length;++i) {
            float actual = newSet[i].getAmplitudeIndB();
            if(actual>max) {
                max = actual;
                maxI = i;
            }
        }
        if(newSet[maxI].getClass().equals(Tone.class)&&maxI!=0) {
            last = newSet[maxI];
            lastTones[lastTone] = newSet[maxI];
            lastTone = (lastTone+1)%toneBufferSize;
            if(lastTone==0&&!firstCycle)
                firstCycle = !firstCycle;
        }
    }
    
    /**
     * Stabilizes the extracted Tone by averaging it with the last few Tones.
     * @return stabilized Tone
     */
    public Tone getTone() {
        if(this.ready()) {            
            // calculate the average frequency to stabilize the output
            float averageFrequency = 0;
            for(int i = 0;i<toneBufferSize;++i) {
                averageFrequency += lastTones[i].getFrequency();
            }
            averageFrequency = averageFrequency/toneBufferSize;
            return new Tone(averageFrequency);
            //return last;
        }
        return null;
    }
    
    public boolean ready() {
        return firstCycle;
    }
}
