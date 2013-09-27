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
    
    private double currentAngle;
    private double oldAngle;
    private static int CYCLE;
    private static int STEPS;
    private long lastStep;
    private int step;
    
    
    PointerDisplay() {
        super();
        angle = 0;
        center = 130;
        this.setPreferredSize( new Dimension(300, 200) ) ;
        
        CYCLE = 10;
        STEPS = 18;
        step = 0;
        lastStep = System.currentTimeMillis();
        currentAngle = 0;
        oldAngle =  0;
    }
    
    /**
     * Set the angle the pointer should have
     * 
     * @param a offset of the Frequency, bigger or equal than -0.5 and smaller or equal than 0.5
     */
    public void setAngle(double a) {
        oldAngle = currentAngle;
        step = 1;
        if(a>=-0.5&&a<=0.5) {
            angle = a*Math.PI;
        }
        
        if( angle < 0 )
        {
            angle += Math.PI;
        }
        
        repaint();
    }
    
    @Override
    public void paintComponent(Graphics g) {
        if( step < STEPS && lastStep+CYCLE < System.currentTimeMillis() ) {
            currentAngle = easeInOut(oldAngle,angle,STEPS,step,1.6);
            step++;
            lastStep = System.currentTimeMillis();
        }
        
        super.paintComponent(g);
        
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                             RenderingHints.VALUE_ANTIALIAS_ON);

        center = this.getWidth()/2;
        drawScale(g2d);

        g2d.setColor(Color.red);
        
        int radius = getHeight() > getWidth() / 2 ? getWidth() / 2 : getHeight()-1;
        int pointerLength = radius - 5;
        
        g2d.drawLine(center, radius, center - (int)(Math.cos(currentAngle)*pointerLength), radius - (int)(Math.sin(currentAngle)*pointerLength));
    }
    
    private void drawScale( Graphics2D g ) {
        
        double ang;
        int radius = getHeight() > getWidth() / 2 ? getWidth() / 2 : getHeight()-1;
        int innerRad = radius - 10;
        int semiRad = radius - 5;
        g.setColor(Color.black);
        
        int x, y;
        
        for(int i = 0; i <= 36; i++)
        {
            ang =  ( i * 5.0 ) / 180.0 * Math.PI;
            
            if( i% 2 == 0)
            {
                x = (int)(Math.cos(ang) * radius);
                y = (int)(Math.sin(ang) * radius);
            }
            else
            {
                x = (int)(Math.cos(ang) * semiRad);
                y = (int)(Math.sin(ang) * semiRad);
            }

            g.drawLine(center - (int)( Math.cos(ang) * innerRad ),
                       radius - (int)( Math.sin(ang) * innerRad ),
                       center - x, radius - y);
        }
    }
    
    private double easeInOut(double minValue,double maxValue,int totalSteps,int actualStep,double powr) { 
        double delta = maxValue - minValue;
	double r = minValue+(Math.pow(((double)actualStep / (double)totalSteps), powr) * delta);
        return r;
    }
}
