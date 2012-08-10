/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
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

    private synchronized void fireEvent(Tone[] freq) {
        TransformedEvent event = new TransformedEvent(this);
        Iterator i = _listeners.iterator();
        while(i.hasNext()) {
            ((TransformedEventListener) i.next()).handleTransformEvent(event,freq);
        }
    }

    private Tone[] freq;
    private double[] data;
    
    public FFT(int startf,int endf) {
        freq = new Tone[endf-startf];
        for(int i= 0;i<endf-startf;i++) {
            freq[i] = new Tone(startf+i);
        }
        data = new double[(int)Math.pow(2,3)];
    }
    
    public void setInput(ByteArrayOutputStream a) {
        if(a!=null) {
            ByteArrayInputStream b = new ByteArrayInputStream(a.toByteArray());
            for(int i=0;i<Math.pow(2,3);i++) {
                data[i] = (double)b.read();
            }
        }
    }
    
    public Tone[] getSpectrum() {
        for(int i=0;i<freq.length;i++) {
            freq[i].setAmplitude(transform(data,i));
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
    private double transform(double[] a,int frequency) {
        if(a.length>1) {
            double[] even = new double[a.length/2];
            double[] odd = new double[a.length/2];
            int m = a.length/2;

            for(int i=0;i<m;i++) {
                int ii = 2*i;
                even[i] = a[ii]*Math.cos((-Math.PI*ii*frequency)/m);
                odd[i] = a[ii+1]*(Math.cos((-Math.PI*(ii+1)*frequency)/m)*Math.cos((-2*Math.PI*(ii+1))/a.length));
            }
            double evenResult = transform(even,frequency);
            double oddResult = transform(odd,frequency);
            double res = evenResult+oddResult;
            
            return res;
        }
        return a[0];
    }
}
