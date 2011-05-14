/*
 * Container for the yoob ContentPane that displays it in a JFrame
 * (native GUI window).  Also provides a main() method.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.ArrayList;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import java.util.concurrent.SynchronousQueue;

import javax.swing.*;

public class GUI {
    private ContentPane cp;
    private SynchronousQueue<Integer> mailbox;
    private String title;

    public GUI(String languageClasses, String selectedLanguage) {
        EsolangLoader el = new EsolangLoader();
	el.load(languageClasses);
        cp = new ContentPane(el, null, selectedLanguage);
        mailbox = new SynchronousQueue<Integer>();
    }
 
    private void init() {
        JFrame frame = new JFrame(cp.getTitle());
        frame.setContentPane(cp);
        final GUI gui = this;
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                gui.close(new Integer(5));
            }
        });
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void display() {
        init();
        try {
            mailbox.take();
        } catch (InterruptedException e) {
            System.out.println("Interrupted.");
        }
    }

    void close(Integer r) {
        try {
            mailbox.put(r);
        } catch (InterruptedException e) {
            System.out.println("Interrupted.");
        }
    }

    /********************** Static Methods *************************/

    public static void main(String[] args) {
        String languageClasses = null;
        String selectedLanguage = null;

        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-c")) {
                i++;
                languageClasses = args[i];
            } else if (args[i].equals("-s")) {
                i++;
                selectedLanguage = args[i];
            } else {
                System.out.println("Usage: java [-cp <classpath>] tc.catseye.yoob.GUI -c <languageclasses> -s <selectedlanguage>");
                System.exit(1);
            }
        }

        GUI gui = new GUI(languageClasses, selectedLanguage);
        gui.display();
        System.exit(0);
    }
}
