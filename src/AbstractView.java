/*
 * An AbstractView is the basis for both PlayfieldViews and TapeViews.
 *
  * The source code in this file has been placed into the public domain.
 */

package tc.catseye.yoob;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;


public abstract class AbstractView implements View {
    public int getPreferredCellWidth() {
        return 1;
    }

    public String getPreferredCellWidthUnits() {
        return "ems";
    }

    public int getPreferredCellHeight() {
        return 1;
    }

    public String getPreferredCellHeightUnits() {
        return "ems";
    }

    public boolean getSquareOff() {
        return false;
    }

    public float getAlignmentX() {
        return Component.CENTER_ALIGNMENT;
    }

    public float getAlignmentY() {
        return Component.CENTER_ALIGNMENT;
    }

    public Color getBackground() {
        return Color.white;
    }

    public Color getForeground() {
        return Color.black;
    }

    public void renderBackground(Graphics g, Element e, int x, int y, int w, int h) {
        g.setColor(e == null ? Color.blue : getBackground());
        g.fillRect(x, y, w, h);
    }

    public void render(Graphics g, Element e, int x, int y, int w, int h) {
        if (e == null) return;
        g.setColor(getForeground());
        String contents = e.getName();
        /*
        if (getPreferredCellWidthUnits() == "ems") {
            int maxlen = getPreferredCellWidth();
            if (contents.length() > maxlen) {
                contents = contents.substring(0, maxlen);
            }
        }
        */
        Font font = g.getFont();
        FontMetrics metrics = g.getFontMetrics(font);
        //int fontHeight = metrics.getHeight();
        int sWidth = metrics.stringWidth(contents);
        while (sWidth > w) {
            contents = contents.substring(0, contents.length() - 1);
            sWidth = metrics.stringWidth(contents);
        }
        // now sWidth <= w
        int offset = (int)((w - sWidth) * getAlignmentX());
        //System.out.printf("offset: %s\n", offset);
        g.drawString(contents, x + offset, y + h);
    }

    abstract public void render(Graphics g, Head p, int x, int y, int w, int h);
    abstract public void render(Graphics g, Cursor c, int x, int y, int w, int h);
}
