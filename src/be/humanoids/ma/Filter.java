/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

/**
 *
 * @author Martin
 */
public class Filter {
    Tone[] lastTones;
    int toneBufferSize = 8;
    
    Filter() {
        lastTones = new Tone[toneBufferSize];
    }
}
