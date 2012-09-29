
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
    byte[] buffer = new byte[10000];
    boolean record = false;
    int samplelength;
    int startf;
    
    TargetDataLine targetDataLine;
    ByteArrayOutputStream data;
    
    TransformedEventListener telistener;
    
    // Event stuff
    private List _listeners = new ArrayList();
    private int skipped = 0;
    private int goal;
    public synchronized void addEventListener(InputEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(InputEventListener listener) {
        _listeners.remove(listener);
    }

    private synchronized void fireEvent(ByteArrayOutputStream a) {
        float[] sound = new float[a.size()];
        ByteArrayInputStream b = new ByteArrayInputStream(a.toByteArray());
        for(int i=0;i<sound.length;i++) {
            float actual = (float)b.read();
            if(actual>128)
                actual-=256;
            sound[i] = actual;
        }

        InputEvent event = new InputEvent(this);
        Iterator i = _listeners.iterator();
        while(i.hasNext()) {
            ((InputEventListener) i.next()).handleInputEvent(event,sound);
        }
        
        // do the FFT at the end because it will screw up the data
        skipped+=sound.length;
        if(skipped>=goal) {
            FFT fourier = new FFT(sound);
            Thread fourierThread = new Thread(fourier);
            fourier.addEventListener(telistener);
            fourierThread.start();
            skipped=0;
        }
    }
    
    AudioRecorderThread(int s, int l) {
        data = new ByteArrayOutputStream();
        samplelength = l;
        startf = s;
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
        try {
            if(targetDataLine==null) {
                System.out.println("No target DataLine defined, can't start to capture it");
            }
            targetDataLine.start();
            while(record) {
                int count = targetDataLine.read(buffer, 0, buffer.length);
                if(count > 0) {
                    data.write(buffer, 0, count);
                    if(data.size()>samplelength) {                        
                        byte[] b = data.toByteArray();
                        byte[] c = new byte[samplelength];
                        int off = data.size()-samplelength;
                        for(int i=0;i<samplelength;++i) {
                            c[i] = b[off+i];
                        }
                        data.reset();
                        data.write(c);
                        fireEvent(data);
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
     * Complete initialization with parameters only available after creating the Thread
     * @param t the TargetDataLine to listen from
     */
    public void setT(TargetDataLine t) {
        targetDataLine = t;
        goal = (int)Math.floor(targetDataLine.getFormat().getFrameRate()/40);
        System.out.println(goal);
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
