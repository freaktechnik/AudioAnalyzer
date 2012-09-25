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
    
    public FFT(float[] a) {
        freq = new Tone[a.length];
        for(int i= 0;i<a.length;i++) {
            freq[i] = new Tone(i);
        }
        data = new float[a.length];
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
        data = padData(data);
        freq = transform(data);
        fireEvent(freq);
        return freq;
    }
    
    /**
     * Recursive implementation of a FFT (wrong, don't use....)
     * @param a the Datapoints of the Audiosample
     * @param frequency the frequency to search
     * @return The amplitude of the searched frequency
     */    
    private float transform(float[] a,int frequency) {
        if(a.length==1) {
            return a[0];
        }
        int m = a.length/2;
        float[] even = new float[m];
        float[] odd = new float[m];

        for(int i=0;i<m;i++) {
            int ii = 2*i;
            even[i] = a[ii];
            odd[i] = a[ii+1];
        }
        float evenResult = transform(even,frequency);
        float oddResult = transform(odd,frequency);
        float res = (float) (evenResult+oddResult*(Math.cos((-2*Math.PI*frequency)/a.length)));

        return res;
    }
    
    /**
     * Non recursive calculation of the FFT. Returning the amplitude of each frequency.
     * @param a the data to transform
     * @return frequency spectrum witht he length of a
     */
    private Tone[] transform(float[] a) {
        if(a.length%2!=0)
            return null;
        
        int m = a.length/2;
        
        // bit reversal sorting by the array indexes
        // first element in a half stays, the other are swapped
        for(int i=1;i<m/2;++i) {
            a = swapPositions(a,i,m-i);
            a = swapPositions(a,i+m,a.length-i);
        }
        
        float[] re = a;
        float[] im = new float[a.length];
        for(int i = 0;i<im.length;++i) {
            im[i]=0;
        }
        int max = (int)(Math.log(a.length)/Math.log(2));
        
        for(int i=0;i<max;++i) {
            int p = (int) Math.pow(2,i);
            int adp = a.length*2/p;
            for(int j=0;j<adp;++j) {
                int z = 0;
                int d = m/p;
                for(int k = 0;k<p;++k) {
                    float arg = (float) (-2*Math.PI*k*z)/a.length;
                    float reodd = (float) (Math.cos(arg)*re[k+p]);
                    float imodd = (float) (Math.sin(arg)*im[k+p]);
                    
                    re[k+p] = re[k]-reodd;
                    im[k+p] = im[k]-imodd;
                    
                    re[k] += reodd;
                    im[k] += imodd;
                    z+=d;
                }
            }
        }

        Tone[] f = new Tone[a.length];
        for(int i=0;i<a.length;++i) {
            f[i] = new Tone(i*22050/a.length,(float)Math.sqrt(Math.pow(re[i],2)+Math.pow(im[i],2)));
        }
        return f;
    }

    private float[] swapPositions(float[] array,int i,int j) {
        float b = array[i];
        array[i] = array[j];
        array[j] = b;
        return array;
    }
    
    private float[] padData(float[] a) {
        int zero = 0;
        for(int i = 0;a[i]!=0;++i) {
            if(a[i]==0)
                zero = i;
        }
        int newl = a.length-zero;
        float[] d = new float[newl*4];
        float c = (float) Math.PI/(2*a.length);
        for(int i =0;i<d.length;++i) {
            if(i<newl)
                d[i] = (float) (gaussianTempering(i,newl)*a[i+zero]);
            else if(i>newl&&i<newl*3)
                d[i] = 0;
            else
                d[i] = -d[d.length-i];
        }
        
        return d;
    }
    
    
    /**
     * Returns the value of the Guassian Tempering Function for the point n
     * @param n the point of the function on the x-Axis
     * @param m the length of the curve
     * @return Value of the Gaussian Tempering Function at the point n
     */
    private double gaussianTempering(int n, int m) {
        int mmo = (m-1)/2;
        return Math.exp(-0.125*((n-mmo)/(0.4*mmo)));
    }
}
