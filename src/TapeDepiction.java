/*
 * A TapeDepiction is an AbstractDepiction that depicts a Tape.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Dimension;

public class TapeDepiction extends AbstractDepiction {
    private Tape t;
    private int min, max, size;
    private int displayMin, displayMax, displaySize;
    private int height, displayHeight, displayOffset;

    public void setTape(Tape t) {
        this.t = t;
    }

    private void recomputeBounds() {
        displayMin = min = t.getMin().intValue();
        displayMax = max = t.getMax().intValue();
      
        for (int i = 0; i < t.numHeads(); i++) {
            Head h = t.getHead(i);
            int pos = h.getPos().intValue();
            if (pos < displayMin) displayMin = pos;
            if (pos > displayMax) displayMax = pos;
        }

        size = (max - min) + 1;
        height = cellHeight * size;
        displaySize = (displayMax - displayMin) + 1;
        displayHeight = cellHeight * displaySize;
        displayOffset = (min - displayMin) * cellHeight;
    }

    public void resize() {
        if (t == null) return;
        recomputeBounds();
        Dimension area = new Dimension(cellWidth + margin * 2, displayHeight + margin * 2);
        setPreferredSize(area);
        revalidate();
        repaint();
    }

    protected void depict(Graphics g) {
        if (t == null || v == null) return;

        g.setColor(Color.blue); // view.getSurroundingColor()
        Rectangle r = g.getClipBounds();
        g.fillRect(r.x, r.y, r.width, r.height);

        recomputeBounds();

        int marginX = margin;
        int marginY = displayOffset + margin;

        g.setColor(Color.black);
        g.drawRect(marginX - 1, marginY - 1,
                   cellWidth + 1, size * cellHeight + 1);

        for (int y = min; y <= max; y++) {
            Element e = t.read(new IntegerElement(y));
            int paintY = (y - min) * cellHeight;
            v.renderBackground(g, e, marginX, marginY + paintY, cellWidth, cellHeight);
        }

        for (int y = min; y <= max; y++) {
            Element e = t.read(new IntegerElement(y));
            int paintY = (y - min) * cellHeight;
            v.render(g, e, marginX, marginY + paintY, cellWidth, cellHeight);
        }
        
        int numHeads = t.numHeads();
        for (int c = 0; c < numHeads; c++) {
            Head head = t.getHead(c);
            int pos = head.getPos().intValue();
            int paintPos = (pos - min) * cellHeight;
            v.render(g, head, marginX, marginY + paintPos, cellWidth, cellHeight);
        }
    }
}
