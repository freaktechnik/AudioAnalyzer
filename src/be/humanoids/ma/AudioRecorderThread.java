
package be.humanoids.ma;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.sound.sampled.TargetDataLine;
/**
 * The audio recorder thread collects the input from the soundcard, byte by byte.
 * @author Martin Giger
 */
public class AudioRecorderThread  implements Runnable
{
    // Event stuff
    private List _listeners = new ArrayList();
    public synchronized void addEventListener(InputEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(InputEventListener listener) {
        _listeners.remove(listener);
    }

    private synchronized void fireEvent(ByteArrayOutputStream a) {
        if(a!=null) {
            float[] sound = new float[a.size()];
            ByteArrayInputStream b = new ByteArrayInputStream(a.toByteArray());
            for(int i=0;i<samplelength;i++) {
                float actual = (float)b.read();
                if(actual>128)
                    actual-=256;
                sound[i] = actual;
            }

            FFT fourier = new FFT(startf,endf,samplelength,sound);
            Thread fourierThread = new Thread(fourier);
            fourier.addEventListener(telistener);
            fourierThread.start();

            InputEvent event = new InputEvent(this);
            Iterator i = _listeners.iterator();
            while(i.hasNext()) {
                ((InputEventListener) i.next()).handleInputEvent(event,sound);
            }
        }
    }

    byte[] buffer = new byte[10000];
    boolean record = false;
    Tone[] freq;
    int samplelength;
    int startf;
    int endf;
    
    TargetDataLine targetDataLine;
    ByteArrayOutputStream data;
    
    TransformedEventListener telistener;
    
    AudioRecorderThread(int s,int e, int l) {
        data = new ByteArrayOutputStream();
        samplelength = l;
        startf = s;
        endf = e;
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
                    fireEvent(data);
                    if(data.size()>=samplelength) {                        
                        data.reset();
                    }
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
    
    public void setEventTarget(TransformedEventListener listener) {
        this.telistener = listener;
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
