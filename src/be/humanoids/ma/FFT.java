/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 *
 * @author Martin
 */
public class FFT {
    // Event stuff
    private List _listeners = new ArrayList();
    public synchronized void addEventListener(TransformedEventListener listener) {
        _listeners.add(listener);
    }
    public synchronized void removeEventListener(TransformedEventListener listener) {
        _listeners.remove(listener);
    }

    private synchronized void fireEvent(Tone[] freq, double[] d) {
        TransformedEvent event = new TransformedEvent(this);
        Iterator i = _listeners.iterator();
        while(i.hasNext()) {
            ((TransformedEventListener) i.next()).handleTransformEvent(event,freq,d);
        }
    }

    private Tone[] freq;
    private double[] data;
    int samplelength;
    int startf;
    
    public FFT(int startf,int endf, int length) {
        freq = new Tone[endf-startf];
        for(int i= 0;i<endf-startf;i++) {
            freq[i] = new Tone(startf+i);
        }
        this.startf = startf;
        data = new double[length];
        samplelength = length;
    }
    
    public void setInput(ByteArrayOutputStream a) {
        // needs a wraparound like in Visualiter.java/createWaveformImage
        if(a!=null) {
            ByteArrayInputStream b = new ByteArrayInputStream(a.toByteArray());
            for(int i=0;i<samplelength;i++) {
                double actual = (double)b.read();
                if(actual>128)
                    actual-=256;
                data[i] = actual;
            }

        }
    }
    
    public Tone[] getSpectrum() {
        for(int i=0;i<freq.length;i++) {
            freq[i].setAmplitude(transform(data,startf+i));
        }
        fireEvent(freq,data);
        return freq;
    }
    
    /**
     * Recursive implementation of a FFT
     * @param a the Datapoints of the Audiosample
     * @param frequency the frequency to search
     * @return The amplitude of the searched frequency
     */    
    private double transform(double[] a,int frequency) {
        if(a.length==1) {
            return a[0];
        }
        double[] even = new double[a.length/2];
        double[] odd = new double[a.length/2];
        int m = a.length/2;

        for(int i=0;i<m;i++) {
            int ii = 2*i;
            even[i] = a[ii];
            odd[i] = a[ii+1];
        }
        double evenResult = transform(even,frequency);
        double oddResult = transform(odd,frequency);
        double res = 0.5*evenResult+0.5*oddResult*(Math.cos((-2*Math.PI*frequency)/a.length));

        return res;
    }
}
