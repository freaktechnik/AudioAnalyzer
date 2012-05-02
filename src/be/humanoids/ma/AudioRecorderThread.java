
package be.humanoids.ma;


import javax.sound.sampled.*;
import java.io.*;
/**
 *
 * @author Martin Giger
 */
public class AudioRecorderThread extends Thread
{
    byte[] buffer = new byte[10000];
    boolean record = false;
    
    TargetDataLine targetDataLine;
    ByteArrayOutputStream data;
    
    AudioRecorderThread(TargetDataLine t) {
        targetDataLine = t;
    }
    
    /**
     * Start the thread and read the stream. but only write if it contains data
     * (If there is an input, there will most likely be data)
     */
    
    @Override
    public void start() {
        record = true;
        
        // always try, since errors can happen!
        try {
            while(record) {
                int count = targetDataLine.read(buffer, 0, buffer.length);
                if(count > 0)
                        data.write(buffer, 0, count);
            }
            data.close();
        }
        catch(IOException e) {
            System.out.println(e);
            System.exit(1);
        }
    }
    
    /**
     * unluckily stop() is already overriden by Thread, so this can't be done here.
     * This stops reading the data from the soundcard and returns the audio data.
     */
    public ByteArrayOutputStream stopRecording() {
        record = false;
        return data;
    }
}
