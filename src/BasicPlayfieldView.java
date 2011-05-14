/*
 * A BasicPlayfieldView is a PlayfieldView suitable for most non-fancy purposes.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;


public class BasicPlayfieldView extends AbstractView {
    public boolean getSquareOff() {
        return true;
    }

    public void render(Graphics g, Cursor c, int x, int y, int w, int h) {
        g.setColor(Color.red);
        g.drawRoundRect(x - 1, y - 1, w + 2, h + 2, w / 4, h / 4);
    }

    public void render(Graphics g, Head p, int x, int y, int w, int h) {
    }
}
