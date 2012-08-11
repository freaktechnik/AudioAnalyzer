/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.util.EventObject;
/**
 *
 * @author Martin
 */
public interface TransformedEventListener {
    public void handleTransformEvent(EventObject e,Tone[] freq);
    public void handleTransformEvent(EventObject e,double[] by);
}
