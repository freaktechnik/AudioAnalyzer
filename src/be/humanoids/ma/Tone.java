package be.humanoids.ma;

/**
 * A tone is essentially a frequency and can be converted into the absolute tone naming scale
 * @author Martin
 */
public class Tone {
    private float frequency;
    private double amplitude;
    private int steps;
    private double off;
    private boolean stepsGenerated;
    private static double baseFrequency = 440.0; // the frequency of the tone the absolute names are relative to
    private static int toneSteps = 12;
    private static final double log2 = Math.log(2.0);
    private static double factor;
    private static final double centFactor = 1200.0/log2;
    private static int offset = 0; // offset in steps to tune diffrent instruments

    
    public Tone(float f) {
        frequency = f;
        amplitude = 0;
        stepsGenerated = false;
        Tone.factor = Tone.toneSteps/Tone.log2;
    }
    
    public Tone(float f, double amp) {
        frequency = f;
        amplitude = amp;
        stepsGenerated = false;
        Tone.factor = Tone.toneSteps/Tone.log2;
    }
    
    /**
     * Calculates the number of steps from the chambertone (baseFrequency)
     */
    private void getSteps() {
        double stepsCalc = Tone.factor*Math.log(frequency/Tone.baseFrequency)%12;
        if(stepsCalc<0) { //handle Tones under the baseFrequency
            stepsCalc = Tone.toneSteps+stepsCalc;
        }
        
        double floorSteps = Math.floor(stepsCalc);
        int endSteps = (int)floorSteps;
        off = stepsCalc-floorSteps;
        if(off>0.5) {
            endSteps++;
            off--;
        }
        steps = endSteps;
        stepsGenerated = true;
        //System.out.println(this.getCents() +" "+frequency+" "+this.getAbsoluteName());
    }
    
    /**
     * Calculates the absolute name from the number of steps
     * @return the character (maybe string for height...) of the absolute name
     */    
    public String getAbsoluteName() {
        return this.getAbsoluteName(0);
    }
    
    /**
     * Calculates the absolute name from the number of steps with a given offset
     * @param offset offset to the name of the original Tone
     * @return absolute name, calculated with offset
     */
    public String getAbsoluteName(int offset) {
        if(!stepsGenerated)
            getSteps();
        switch((steps+Tone.offset+offset)%Tone.toneSteps) {
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
    
    /**
     * Calculates the number of cents the tone is off the detected Tone
     * @return number of cents off the tone
     */
    public double getCents() {
        if(!stepsGenerated)
            getSteps();
        return Math.log(frequency/(frequency-off))*centFactor;
    }
    
    public double getOffset() {
        return off;
    }
    
    public void setAmplitude(float amp) {
        amplitude = amp;
    }
    
    public double getAmplitude() {
        return amplitude;
    }
    
    public float getAmplitudeIndB() {
        return (float) (10*Math.log10(amplitude));
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
        hash = 59 * hash + Float.floatToIntBits(this.frequency);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.amplitude) ^ (Double.doubleToLongBits(this.amplitude) >>> 32));
        return hash;
    }

    
    @Override
    public String toString() {
        return this.getAbsoluteName();
    }
    
    /**
     * Returns absolute names relative to the own one.
     * @param Offset in steps to the Tone
     * @return actual name with the specified offset
     */
    public String getRelativeAbsName(int off) {
        return this.getAbsoluteName(off);
    }
    
    public String toCSVString() {
        return frequency+";"+amplitude+"\n";
    }
    
    /* static methods */
    
    public static double getBaseFrequency() {
        return Tone.baseFrequency;
    }
    
    public static int getOffsetInSteps() {
        return Tone.offset;
    }
    
    public static int getToneSteps() {
        return Tone.toneSteps;
    }
    
    public static void setOffsetInSteps(int newOffset) {
        Tone.offset = newOffset;
    }
    
    public static void setBaseFrequency(double newF) {
        if(newF>0)
            Tone.baseFrequency = newF;
    }
    public static void setToneSteps(int newStepCount) {
        if(newStepCount>0) {
            Tone.toneSteps = newStepCount;
            Tone.factor = Tone.toneSteps/Tone.log2;
        }
    }
}
