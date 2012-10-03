/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import java.awt.GraphicsDevice.WindowTranslucency.*;

/**
 * A panel to draw a pointer for the offset display
 * @author Martin
 */
public class PointerDisplay extends JPanel{
    private BufferedImage pointer;
    private double angle;
    
    
    PointerDisplay() {
        super();
        ImageIcon icon = new ImageIcon(getClass().getResource("/assets/pointer_p.png"));
        Image source = icon.getImage();
        int w = source.getWidth(null);
        int h = source.getHeight(null);
        pointer = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = (Graphics2D)pointer.getGraphics();
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        angle = 0;
        Dimension d = new Dimension(259,133);
        this.setSize(d);
        this.setPreferredSize(d);
    }
    
    /**
     * Set the angle the pointer should have
     * @param a offset of the Frequency, bigger or equal than -0.5 and smaller or equal than 0.5
     */
    public void setAngle(double a) {
        if(a>=-0.5&&a<=0.5)
            angle = a*Math.PI;
        this.paintComponent(this.getGraphics());
    }
    
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                         RenderingHints.VALUE_ANTIALIAS_ON);
        int center = this.getWidth()/2;
        g2d.translate(center, this.getHeight()-4);
        g2d.rotate(angle);

        g2d.drawImage(pointer, -4, -this.getHeight()+4, this);
    }
}
