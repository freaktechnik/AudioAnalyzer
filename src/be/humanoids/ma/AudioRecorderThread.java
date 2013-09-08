package be.humanoids.ma;


import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sound.sampled.TargetDataLine;
/**
 * The audio recorder thread collects the input from the soundcard, byte by byte.
 * @author Martin Giger
 */
public class AudioRecorderThread  implements Runnable
{
    byte[] buffer = new byte[10000];
    boolean record = false;
    int samplelength;
    int startf;
    ExecutorService fftPool;
    
    TargetDataLine targetDataLine;
    
    TransformedEventListener telistener;
    
    // Event stuff
    private List<InputEventListener> _listeners = new ArrayList<>();
    private int skipped = 0;
    private int goal;
    public synchronized void addEventListener(InputEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(InputEventListener listener) {
        _listeners.remove(listener);
    }

    private synchronized void fireEvent(byte[] a) {
        float[] sound = new float[a.length];
        for(int i=0;i<sound.length;i++) {
            sound[i] = (float)a[i];
            if(sound[i]>=128)
                sound[i] -= 255;
        }

        InputEvent event = new InputEvent(this);
        Iterator<InputEventListener> i = _listeners.iterator();
        while(i.hasNext()) {
            ((InputEventListener) i.next()).handleInputEvent(event,sound);
        }
        
        // do the FFT at the end because it will screw up the data
        skipped+=sound.length;
        if(skipped>=goal) {
            FFT fourier = new FFT(sound);
            fourier.addEventListener(telistener);
            
            fftPool.execute( fourier );
            skipped=0;
        }
    }
    
    AudioRecorderThread(int s, int l) {
        samplelength = l;
        startf = s;
        
        fftPool = Executors.newFixedThreadPool( 10 );
    }
    
    /**
     * Start the thread and read the stream. But only write if it contains data.
     * (If there is an input, there will most likely be data)
     * Fires every samplelength a data event and resets the data.
     */
    @Override
    public void run() {
        record = true;
        // always try, since errors can happen!
        if(targetDataLine==null) {
            System.out.println("No target DataLine defined, can't start to capture it");
        }
        targetDataLine.start();
        int count,off;
        byte[] c;
        c = new byte[samplelength];
        while(record) {
            count = targetDataLine.read(buffer, 0, buffer.length);
            if(count > 0) {
                if(buffer.length>=samplelength) {                        
                    off = buffer.length-samplelength;
                    for(int i=0;i<samplelength;++i) {
                        c[i] = buffer[off+i];
                    }
                    fireEvent(c);
                }
            }
        }
        targetDataLine.stop();
    }
    
    /**
     * Complete initialization with parameters only available after creating the Thread
     * 
     * @param t the TargetDataLine to listen from
     */
    public void setT(TargetDataLine t) {
        targetDataLine = t;
        goal = 0;
    }
    
    public void setEventTarget(TransformedEventListener listener) {
        this.telistener = listener;
    }
    
    /**
     * This stops reading the data from the soundcard.
     * 
     * @return audio data.
     */
    public byte[] stop() {
        record = false;
        return buffer;
    }
}
