/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 *
 * @author Martin
 */
public class FFT implements Runnable {
    // Event stuff
    private List _listeners = new ArrayList();
    public synchronized void addEventListener(TransformedEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(TransformedEventListener listener) {
        _listeners.remove(listener);
    }

    private synchronized void fireEvent(Tone[] freq) {
        TransformedEvent event = new TransformedEvent(this);
        Iterator i = _listeners.iterator();
        while(i.hasNext()) {
            ((TransformedEventListener) i.next()).handleTransformEvent(event,freq);
        }
    }

    private Tone[] freq;
    private float[] data;
    int samplelength;
    int startf;
    
    public FFT(int startf,int endf, int length, float[] a) {
        freq = new Tone[endf-startf];
        for(int i= 0;i<endf-startf;i++) {
            freq[i] = new Tone(startf+i);
        }
        this.startf = startf;
        data = new float[length];
        samplelength = length;
        setInput(a);
    }
    
    final void setInput(float[] a) {
        data = a;
    }
    
    @Override
    public void run() {
        getSpectrum();
    }
    
    public Tone[] getSpectrum() {
        for(int i=0;i<freq.length;i++) {
            freq[i].setAmplitude(transform(data,startf+i));
        }
        fireEvent(freq);
        return freq;
    }
    
    /**
     * Recursive implementation of a FFT
     * @param a the Datapoints of the Audiosample
     * @param frequency the frequency to search
     * @return The amplitude of the searched frequency
     */    
    private float transform(float[] a,int frequency) {
        if(a.length==1) {
            return a[0];
        }
        float[] even = new float[a.length/2];
        float[] odd = new float[a.length/2];
        int m = a.length/2;

        for(int i=0;i<m;i++) {
            int ii = 2*i;
            even[i] = a[ii];
            odd[i] = a[ii+1];
        }
        float evenResult = transform(even,frequency);
        float oddResult = transform(odd,frequency);
        float res = (float) (0.5*evenResult+0.5*oddResult*(Math.cos((-2*Math.PI*frequency)/a.length)));

        return res;
    }
}
