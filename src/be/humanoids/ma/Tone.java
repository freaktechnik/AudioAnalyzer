package be.humanoids.ma;

/**
 * A tone is essentially a frequency and can be converted into the absolute tone naming scale
 * @author Martin
 */
public class Tone {
    private float frequency;
    private float amplitude;
    private int steps;
    private double off;
    private boolean stepsGenerated;
    private static float baseFrequency = 440;
    static final int toneSteps = 12;
    static final double factor = (float) (toneSteps/Math.log(2));
    
    public final float baseTone = 440; // the frequency of the tone the absolute names are relative to
    
    public Tone(float f) {
        frequency = f;
        amplitude = 0;
        stepsGenerated = false;
    }
    
    public Tone(float f, float amp) {
        frequency = f;
        amplitude = amp;
        stepsGenerated = false;
    }
    
    private void getSteps() {
        double stepsCalc = (Tone.factor*Math.log(frequency/Tone.baseFrequency))%12;
        
        double floorSteps = Math.floor(stepsCalc);
        int endSteps = (int)floorSteps;
        off = stepsCalc-floorSteps;
        if(off>0.5) {
            endSteps++;
            off = stepsCalc-endSteps;
        }
        
        steps = endSteps;
        stepsGenerated = true;
    }
    
    /**
     * Calculates the absolute name from the frequency
     * @return the character (maybe string for height...) of the absolute name
     */    
    public String getAbsoluteName() {
        if(!stepsGenerated)
            getSteps();
        getSteps();
        switch(steps) {
            case 11: return "g#";
            case 10: return "g";
            case 9: return "f#";
            case 8: return "f";
            case 7: return "e";
            case 6: return "d#";
            case 5: return "d";
            case 4: return "c#";
            case 3: return "c";
            case 2: return "h";
            case 1: return "a#";
            default: return "a";
        }
    }
    
    public void setAmplitude(float amp) {
        amplitude = amp;
    }
    
    public float getAmplitude() {
        return amplitude;
    }
    
    public float getFrequency() {
        return frequency;
    }
    
    @Override
    public boolean equals(Object a) {
        if(a.getClass()==Tone.class&&a.hashCode()==this.hashCode()) {
            return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Float.floatToIntBits(this.frequency);
        hash = 41 * hash + Float.floatToIntBits(this.amplitude);
        return hash;
    }
    
    public void setBaseFrequency(float f) {
        Tone.baseFrequency = f;
        stepsGenerated = false;
    }
}
