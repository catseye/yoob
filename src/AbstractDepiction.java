/*
 * An AbstractDepiction is the base for all Depictions
 * (JComponents that render Elements using a View.)
 *
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JPanel;

public abstract class AbstractDepiction extends JPanel {
    protected View v;
    protected int cellHeight = 8;
    protected int cellWidth = 8;
    protected int margin;
    protected double zoom = 1.0;

    public AbstractDepiction() {
        margin = 4;
    }

    public void setView(View v) {
        this.v = v;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (v == null) return;
        Font font = g.getFont();
        FontMetrics metrics = g.getFontMetrics(font);
        int fontHeight = metrics.getHeight();
        int fontWidth = metrics.stringWidth("@");
        cellWidth = v.getPreferredCellWidth();
        if (v.getPreferredCellWidthUnits().equals("ems")) {
            cellWidth *= fontWidth;
        }
        cellHeight = v.getPreferredCellHeight();
        if (v.getPreferredCellHeightUnits().equals("ems")) {
            cellHeight *= fontHeight;
        }
        cellWidth *= zoom;
        cellHeight *= zoom;
        if (v.getSquareOff()) {
            if (cellHeight > cellWidth) cellWidth = cellHeight;
            if (cellWidth > cellHeight) cellHeight = cellWidth;
        }
        depict(g);
    }

    abstract public void resize();
    abstract protected void depict(Graphics g);
}
