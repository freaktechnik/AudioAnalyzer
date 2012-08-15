package be.humanoids.ma;

/**
 * A tone is essentially a frequency and can be converted into the absolute tone naming scale
 * @author Martin
 */
public class Tone {
    private float frequency;
    private float amplitude;
    
    public Tone(float f) {
        frequency = f;
    }
    
    /**
     * Calculates the absolute name from the frequency
     * @return the character (maybe string for height...) of the absolute name
     */
    public char getAbsoluteName() {
        return 'a';
    }
    
    public void setAmplitude(float amp) {
        amplitude = amp;
    }
    
    public float getAplitude() {
        return amplitude;
    }
}
