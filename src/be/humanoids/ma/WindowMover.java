/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/**
 *
 * @author Martin
 */
public class WindowMover implements MouseListener, MouseMotionListener {
    private Point dragStart;
    
    private WindowMover(Component window) {
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
    }
    
    public static void addMoving(Component window) {
        WindowMover mover = new WindowMover(window);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        dragStart = e.getPoint();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        
    }
    
    @Override
    public void mouseDragged(MouseEvent e) {
        Point actual = e.getLocationOnScreen();
        Component window = e.getComponent();
        window.setLocation((int)(actual.getX()-dragStart.getX()),(int)(actual.getY()-dragStart.getY()));
        window.repaint();
    }
    
    @Override
    public void mouseMoved(MouseEvent e) {
        
    }
    
    
}
