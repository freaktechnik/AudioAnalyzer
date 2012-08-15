/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;


import java.util.Date;
/**
 *
 * @author Martin
 */
public class Timer {
    private long started;
    private long stopped;
    private long duration;
    
    Timer() {
        reset();
    }
    
    public void start() {
        reset();
        started = System.currentTimeMillis();
    }
    
    public void reset() {
        started = 0;
        duration = 0;
        stopped = 0;
    }
    
    public long actualDuration() {
        return System.currentTimeMillis()-started;
    }
    
    public long stop() {
        stopped = System.currentTimeMillis();
        duration = stopped-started;        
        return duration;
    }
    
    public double toHerz() {
        return 1000.0/duration;
    }
    
    public long getDuration() {
        return duration;
    }
    
    public boolean running() {
        return started>0;
    }
}
