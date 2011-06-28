/*
 * An InputTextArea is a JTextArea which supports being used as the
 * primary input for a running program in an esolang.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JTextArea;

public class InputTextArea extends JTextArea {
    public InputTextArea() {
        super();
        addKeyListener(new KeyListener() {
            public void keyTyped(KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED && !e.isControlDown()) {
                    Character c = new Character(e.getKeyChar());
                    append(c.toString());
                    e.consume();
                    setBackground(Color.white);
                    setCaretPosition(getDocument().getLength());
                }
            }
            public void keyPressed(KeyEvent e) {
            }
            public void keyReleased(KeyEvent e) {
                if (e.getID() != KeyEvent.KEY_TYPED) {
                    if (e.getKeyCode() == (int)'0' && e.isControlDown()) {
                        append("\0");
                        setBackground(Color.white);
                    }
                }
            }
        });
    }
}
