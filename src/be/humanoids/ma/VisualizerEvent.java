/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.awt.Image;

/**
 *
 * @author Martin
 */
public class VisualizerEvent extends java.util.EventObject {
    Image img;
    public VisualizerEvent(Object source, Image i) {
        super(source);
        img = i;
    }
}