package be.humanoids.ma;

/**
 * A tone is essentially a frequency and can be converted into the absolute tone naming scale
 * @author Martin
 */
public class Tone {
    private float frequency;
    private double amplitude;
    
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
    
    public void setAmplitude(double amp) {
        amplitude = amp;
    }
    
    public double getAplitude() {
        return amplitude;
    }
}
