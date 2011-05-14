/*
 * A PlayfieldDepiction is an AbstractDepiction that depicts a Playfield.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Dimension;

public class PlayfieldDepiction extends AbstractDepiction {
    private Playfield p;
    private int minX, minY, maxX, maxY;
    private int displayMinX, displayMinY, displayMaxX, displayMaxY;
    private int sizeX, sizeY;
    private int width, height;
    private int displaySizeX, displaySizeY;
    private int displayWidth, displayHeight;
    private int displayOffsetX, displayOffsetY;

    public void setPlayfield(Playfield p) {
        this.p = p;
    }

    private void recomputeBounds() {
        displayMinX = minX = p.getMinX().intValue();
        displayMinY = minY = p.getMinY().intValue();
        displayMaxX = maxX = p.getMaxX().intValue();
        displayMaxY = maxY = p.getMaxY().intValue();
      
        for (int i = 0; i < p.numCursors(); i++) {
            Cursor c = p.getCursor(i);
            int cx = c.getX().intValue();
            int cy = c.getY().intValue();
            if (cx > displayMaxX) displayMaxX = cx;
            if (cx < displayMinX) displayMinX = cx;
            if (cy > displayMaxY) displayMaxY = cy;
            if (cy < displayMinY) displayMinY = cy;
        }

        sizeX = (maxX - minX) + 1;
        sizeY = (maxY - minY) + 1;
        width = cellWidth * sizeX;
        height = cellHeight * sizeY;

        displaySizeX = (displayMaxX - displayMinX) + 1;
        displaySizeY = (displayMaxY - displayMinY) + 1;
        displayWidth = cellWidth * displaySizeX;
        displayHeight = cellHeight * displaySizeY;

        displayOffsetX = (minX - displayMinX) * cellWidth;
        displayOffsetY = (minY - displayMinY) * cellHeight;
    }

    public void resize() {
        if (p == null) return;
        recomputeBounds();
        Dimension area = new Dimension(displayWidth + margin * 2, displayHeight + margin * 2);
        setPreferredSize(area);
        revalidate();
        repaint();
    }

    protected void depict(Graphics g) {
        if (p == null || v == null) return;

        g.setColor(Color.yellow); // view.getSurroundingColor()
        Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);

        recomputeBounds();

        int marginX = displayOffsetX + margin;
        int marginY = displayOffsetY + margin;
        g.setColor(Color.black); // view.getBorderColor()
        g.drawRect(marginX - 1, marginY - 1, width + 1, height + 1);

        for (int y = minY; y <= maxY; y++) {
            int paintY = (y - minY) * cellHeight;
            for (int x = minX; x <= maxX; x++) {
                Element e = p.get(new IntegerElement(x), new IntegerElement(y));
                int paintX = (x - minX) * cellWidth;
                v.renderBackground(g, e, marginX + paintX, marginY + paintY, cellWidth, cellHeight);
            }
        }

        for (int y = minY; y <= maxY; y++) {
            int paintY = (y - minY) * cellHeight;
            for (int x = minX; x <= maxX; x++) {
                Element e = p.get(new IntegerElement(x), new IntegerElement(y));
                int paintX = (x - minX) * cellWidth;
                v.render(g, e, marginX + paintX, marginY + paintY, cellWidth, cellHeight);
            }
        }
        
        int numCursors = p.numCursors();
        for (int c = 0; c < numCursors; c++) {
            Cursor cursor = p.getCursor(c);
            int cx = cursor.getX().intValue();
            int cy = cursor.getY().intValue();
            int paintX = (cx - minX) * cellWidth;
            int paintY = (cy - minY) * cellHeight;
            v.render(g, cursor, marginX + paintX, marginY + paintY, cellWidth, cellHeight);
        }
    }
}
