
package be.humanoids.ma;


import javax.sound.sampled.*;
import java.io.*;
/**
 *
 * @author Martin Giger
 */
public class AudioRecorderThread  implements Runnable
{
    byte[] buffer = new byte[10000];
    boolean record = false;
    Thread T;
    
    TargetDataLine targetDataLine;
    ByteArrayOutputStream data;
    
    AudioRecorderThread() {
        T = new Thread(this);
        data = new ByteArrayOutputStream();
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
                System.out.println("here we goo!");
            }
            targetDataLine.start();
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
    
    public void setT(TargetDataLine t) {
        targetDataLine = t;
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
