package be.humanoids.ma;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JPanel;

/**
 * A panel to draw a pointer for the offset display
 * @author Martin
 */
public class PointerDisplay extends JPanel{
    private double angle;
    private int center;
    
    
    PointerDisplay() {
        super();
        angle = 0;
        center = 130;
        this.setPreferredSize( new Dimension(300, 200) ) ;
    }
    
    /**
     * Set the angle the pointer should have
     * 
     * @param a offset of the Frequency, bigger or equal than -0.5 and smaller or equal than 0.5
     */
    public void setAngle(double a) {
        if(a>=-0.5&&a<=0.5) {
            angle = a*Math.PI;
        }
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);
        
        drawScale(g2d);
        
        center = this.getWidth()/2;
        g2d.setColor(Color.red);
        g2d.drawLine(center, getHeight(), (int)Math.sin(angle)*getHeight(), (int)Math.cos(angle)*getHeight());
    }
    
    private void drawScale( Graphics2D g ) {
        
        double angle = 0;
        int length = 10;
        int height = getHeight();
        g.setColor(Color.black);
        for(int i = 0; i < 18; i++)
        {
            angle =  i * 10 / Math.PI;
            g.drawLine((int)Math.sin(angle)*(height-length),
                       (int)Math.cos(angle)*(height-length),
                       (int)Math.sin(angle)*height,
                       (int)Math.cos(angle)*height);
        }
    }
}
