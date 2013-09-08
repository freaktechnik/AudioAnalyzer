/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package be.humanoids.ma;

import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author Martin
 */
public class AboutDialog extends JDialog {
    
    public AboutDialog(JFrame parent) {
        super(parent, "About", true);
        
        GridLayout l = new GridLayout( 3, 1, 10, 10);
        JPanel content = new JPanel();
        content.setLayout( l );
        
        content.add(new JLabel("PerfectTone was written by Martin Giger"));
        content.add( new JLabel("Version: 2.0"));

        JButton sourceLink = new JButton();
        sourceLink.setText("<HTML><FONT color=\"#000099\"><U>Source Code</></FONT></HTML>");
        sourceLink.addActionListener(new ActionListener() {
              @Override
	      public void actionPerformed(ActionEvent e) {
                  if(Desktop.isDesktopSupported()) {
                      try {
                          Desktop.getDesktop().browse(new URI("https://github.com/freaktechnik/AudioAnalyzer"));
                      }
                      catch(URISyntaxException | IOException ex) {
                          // nuthin'
                      }
                  }
	      }
        });
        
        content.add(sourceLink);
        
        getContentPane().add(content, "North");
       
        JButton ok = new JButton("Close");
        ok.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setVisible(false);
            }
        });
        getContentPane().add(ok, "Center");
        pack();
    }
    
}
