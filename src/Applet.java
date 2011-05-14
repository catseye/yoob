/*
 * Container for the yoob ContentPane that displays it in an Applet.
 *
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.ArrayList;

import javax.swing.JApplet;
import javax.swing.BorderFactory;
import java.awt.Color;

public class Applet extends JApplet {
    private ContentPane cp;

    public Applet() {
    }
    
    public void init() {
        try {
            javax.swing.SwingUtilities.invokeAndWait(new Runnable() {
                public void run() {
                    createGUI();
                }
            });
        } catch (Exception e) {
            System.err.println("createGUI didn't successfully complete");
        }
    }

    private void createGUI() {
        String languageClasses = getParameter("languageClasses");
        String selectedLanguage = getParameter("selectedLanguage");
        EsolangLoader el = new EsolangLoader();
	el.load(languageClasses);
        cp = new ContentPane(el, this, selectedLanguage);
        cp.setBorder(BorderFactory.createMatteBorder(1,1,1,1,Color.black));
        this.setContentPane(cp);
    }
}
