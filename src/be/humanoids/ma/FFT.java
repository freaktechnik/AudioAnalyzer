package be.humanoids.ma;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
/**
 * An FFT object preforms FFTs over a float array and fires an event when done.
 * It runs in a separate thread.
 * 
 * @author Martin
 */
public class FFT implements Runnable {
    // Event stuff
    @SuppressWarnings("unchecked")
    private List<TransformedEventListener> _listeners = new ArrayList();
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
    private int[] reversal;
    private static final double log2 = Math.log(2);
    
    public FFT(float[] a) {
        freq = new Tone[a.length];
        reversal = new int[(a.length*2)*2]; // quicklist for reversals (shouldn't be needed, tought)
        for(int i= 0;i<a.length;i++) {
            freq[i] = new Tone(i);
            reversal[i] = -1;
        }
        reversal[0] = 0;
        data = a;
    }
    
    @Override
    public void run() {
        getSpectrum();
    }
    
    /**
     * 
     * @return spectrum of the frequencies
     */
    public Tone[] getSpectrum() {
        data = padData(data);
        freq = transform(data);
        fireEvent(freq);
        return freq;
    }
    
    /**
     * Recursive implementation of a FFT (wrong, don't use....)
     * 
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
     * 
     * @param a the data to transform
     * @return frequency spectrum witht he length of a
     */
    private Tone[] transform(float[] a) {
        if(a.length%2!=0)
            return null;
        
        int max = (int)(Math.log(a.length)/log2);
        
        boolean[] check = new boolean[a.length];
        for(int i=0;i<a.length;++i) {
            check[i] = false;
        }
        // bit reversal sorting by the array indexes
        int reversei;
        for(int i=0;i<a.length;++i) {
            if(!check[i]) {
                reversei = reverse(i,max);
                check[i] = true;
                check[reversei] = true;
                a = swapPositions(a,i,reversei);
            }
        }
        // initialize imaginary array
        float[] im = new float[a.length];
        for(int i=0;i<im.length;++i) {
            im[i] = 0;
        }
        
        int p,pn,adp,jp,jpp,kp;
        float arg,recos,imsin,reodd,imodd;
        // butterfly
        for(int i=0;i<max;++i) {
            p = (int) Math.pow(2,i);
            pn = p*2;
            adp = a.length/pn;
            for(int j=0;j<adp;++j) {
                jp = j*pn;
                 jpp = jp+p;
                for(int k = jp;k<jpp;++k) {
                    arg = (float) (-Math.PI*(k-jp))/p;
                    recos = (float) Math.cos(arg);
                    imsin = (float) Math.sin(arg);
                    kp = k+p;
                    reodd = recos*a[kp]-imsin*im[kp];
                    imodd = imsin*a[kp]+recos*im[kp];
                    
                    a[kp] = a[k]-reodd;
                    im[kp] = im[k]-imodd;
                    
                    a[k] += reodd;
                    im[k] += imodd;
                }
            }
        }
        
        // create Tones, thanks to padding only valid until m
        int m = a.length/4;
        double newre,newim;
        Tone[] f = new Tone[m];
        for(int i=0;i<m;++i) {
            newre = a[i]/a.length;
            newim = im[i]/a.length;
            f[i] = new Tone((i*22050.0F)/a.length,Math.sqrt(newre*newre+newim*newim));
        }
        return f;
    }
    
    /**
     * Swaps two elements positions in an array
     * 
     * @param array array the two elements are in
     * @param i index of first element
     * @param j index of second element
     * @return array with swapped elements
     */
    private float[] swapPositions(float[] array,int i,int j) {
        if(i==j)
            return array;
        float b = array[i];
        array[i] = array[j];
        array[j] = b;
        return array;
    }
    
    /**
     * Bit reversal function
     * 
     * @param i bytes to reverse
     * @param length number of bits to reverse
     * @return reversed bytes
     */
    private int reverse(int i,int length) {
        if(reversal[i]!=-1) {
            return reversal[i];
        }
        
        length--;
        int irev = 0;
        for(i>>=1; i!=0; i>>=1)
        {
            irev <<= 1;
            irev |= i & 1;
            length--;
        }

        irev <<= length; // add the avoided zeros
        
        reversal[i] = irev;
        reversal[irev] = i;

        return irev;
    }
    
    /**
     * To make the input periodic, the data needs to be adjusted a bit. To do
     * so, this applies a gaussian function to the data and adds 3 times the
     * length zeros after that
     * 
     * @param a data as float array to be adjusted
     * @return adjusted float array
     */
    private float[] padData(float[] a) {
        float[] d = new float[a.length*4];
        
        for(int i =0;i<d.length;++i) {
            if(i<a.length) {
                d[i] = (float) (gaussianTempering(i,a.length)*a[i]);
            }
            else
                d[i] = 0;
        }
        
        return d;
    }
    
    
    /**
     * Returns the value of the Guassian Tempering Function for the point n
     * 
     * @param n the point of the function on the x-Axis
     * @param m the length of the curve
     * @return Value of the Gaussian Tempering Function at the point n
     */
    private double gaussianTempering(int n, int m) {
        int mmo = (m-1)/2;
        return Math.exp(-0.6*((n-mmo)/(0.4*mmo)));
    }
}
