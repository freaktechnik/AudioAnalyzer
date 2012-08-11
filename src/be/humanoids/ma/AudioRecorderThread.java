
package be.humanoids.ma;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.sound.sampled.TargetDataLine;
/**
 * The audio recorder thread collects the input from the soundcard, byte by byte.
 * @author Martin Giger
 */
public class AudioRecorderThread  implements Runnable
{
    byte[] buffer = new byte[10000];
    boolean record = false;
    Tone[] freq;
    public FFT fourier;
    int samplelength;
    
    TargetDataLine targetDataLine;
    ByteArrayOutputStream data;
    
    AudioRecorderThread(int s,int e, int l) {
        data = new ByteArrayOutputStream();
        samplelength = l;
        fourier = new FFT(s,e,l);
    }
    
    /**
     * Start the thread and read the stream. but only write if it contains data
     * (If there is an input, there will most likely be data)
     */
    
    @Override
    public void run() {
        record = true;
        // always try, since errors can happen!
        try {
            if(targetDataLine==null) {
                System.out.println("No target DataLine defined, can't start to capture it");
            }
            targetDataLine.start();
            while(record) {
                int count = targetDataLine.read(buffer, 0, buffer.length);
                if(count > 0) {
                    data.write(buffer, 0, count);
                }
                if(data.size()>=samplelength) {
                    fourier.setInput(data);
                    fourier.getSpectrum();
                    data.reset();
                }
            }
            data.close();
            targetDataLine.close();
        }
        catch(IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
    
    /**
     * Set the targetDataLine once it is set
     * @param t the TargetDataLine you want to use
     */
    
    public void setT(TargetDataLine t, Tone[] a) {
        targetDataLine = t;
        freq = a;
    }
    
    /**
     * This stops reading the data from the soundcard.
     * @return audio data.
     */
    public ByteArrayOutputStream stop() {
        record = false;
        return data;
    }
}
