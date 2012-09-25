/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;


import java.util.Date;
/**
 * A simple class to mesure the time consumed in a program
 * @author Martin
 */
public class Timer {
    private long started;
    private long stopped;
    private long duration;
    
    Timer() {
        reset();
    }
    
    /**
     * Starts the timer; resets before starting
     */
    public void start() {
        reset();
        started = System.currentTimeMillis();
    }
    
    /**
     * Resets the timer to 0
     */
    public void reset() {
        started = 0;
        duration = 0;
        stopped = 0;
    }
    
    /**
     * Outputs the duration the timer is running, without pausing or stopping it
     * @return the time in Miliseconds since the Timer started
     */
    public long actualDuration() {
        return System.currentTimeMillis()-started;
    }
    
    /**
     * Stops the Timer and calculates the duration
     * @return duration in miliseconds
     */
    public long stop() {
        stopped = System.currentTimeMillis();
        duration = stopped-started;        
        return duration;
    }
    
    /**
     * Used for example to mesure a framerate
     * @return Herz of the Time
     */
    public double toHerz() {
        return 1000.0/duration;
    }
    
    /**
     * Returns the duration when the Timer is stopped
     * @return last duration in miliseconds
     */
    public long getDuration() {
        return duration;
    }
    
    /**
     * Used to check if the timer is running. Doesn't work when the Timer is started at the local time 0 in Miliseconds
     * @return true if the timer is runnign, false when not
     */
    public boolean running() {
        return started>0;
    }
}
