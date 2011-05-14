/*
 * A View describes how to render elements onto a graphics context.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.awt.Graphics;
import java.awt.Color;

public interface View {
    int     getPreferredCellWidth();
    /*
     * This can be "pixels" or "ems"
     */
    String  getPreferredCellWidthUnits();
    int     getPreferredCellHeight();
    /*
     * This can be "pixels" or "ems"
     */
    String  getPreferredCellHeightUnits();
    /*
     * If true, the cell is made square by expanding the smaller dimension
     * to match the larger one.
     */
    boolean getSquareOff();
    /*
     * These are compatible with the java.awt.Component.*_ALIGNMENT constants
     */
    float   getAlignmentX();
    float   getAlignmentY();
    Color   getBackground();
    Color   getForeground();
    void    renderBackground(Graphics g, Element e, int x, int y, int w, int h);
    void    render(Graphics g, Element e, int x, int y, int w, int h);
    void    render(Graphics g, Cursor c, int x, int y, int w, int h);
    void    render(Graphics g, Head p, int x, int y, int w, int h);
}
