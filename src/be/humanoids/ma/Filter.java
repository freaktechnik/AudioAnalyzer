package be.humanoids.ma;

/**
 * Used to get the correct Tone for tuning and stabilizing the output
 * @author Martin
 */
public class Filter {
    private Tone[] lastTones;
    private final static int toneBufferSize = 3;
    private int lastTone;
    private boolean firstCycle;
    
    private static boolean EnableHPS = false;
    
    Filter() {
        lastTones = new Tone[toneBufferSize];
        firstCycle = false;
        lastTone = 0;
    }
    
    /**
     * Adds a new tone from a frequency spectrum to the Buffer
     * 
     * @param newSet Spectrum the Tone gets extracted from
     */
    public void setNewTone(Tone[] newSet) {
        if( EnableHPS ) {
            HPS newTone = new HPS(newSet,3);
            lastTones[lastTone] = newTone.getTone();
        }
        else {
            int maxI = 0;
            double max = 0;
            double actual;
            for(int i=1;i<newSet.length;++i) {
                actual = newSet[i].getAmplitude();
                if(actual>max) {
                    max = actual;
                    maxI = i;
                }
            }
            lastTones[lastTone] = newSet[maxI];
        }
        lastTone = (lastTone+1)%Filter.toneBufferSize;
        if(lastTone==0&&!firstCycle)
            firstCycle = !firstCycle;
    }
    
    /**
     * Stabilizes the extracted Tone by averaging it with the last few Tones.
     * 
     * @return stabilized Tone
     */
    public Tone getTone() {
        float averageFrequency = 0;
        if(this.ready()) {            
            // calculate the average frequency to stabilize the output
            for(int i = 0;i<Filter.toneBufferSize;++i) {
                averageFrequency += lastTones[i].getFrequency();
            }
            averageFrequency = averageFrequency/Filter.toneBufferSize;
        }
        else {
            for(int i =0;i<lastTone;++i) {
                averageFrequency += lastTones[i].getFrequency();
            }
            if(lastTone>0)
                averageFrequency = averageFrequency/lastTone;
        }
        return new Tone(averageFrequency);
    }
    
    public boolean ready() {
        return firstCycle;
    }
    
    public void reset() {
        firstCycle = false;
        lastTone = 0;
    }
    
    public static void EnableHPS() {
        EnableHPS = true;
    }
    
    public static void DisableHPS() {
        EnableHPS = false;
    }
    
    public static void ToggleHPS() {
        EnableHPS = !EnableHPS;
    }
}
