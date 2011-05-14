/*
 * An OverlayPlayfield is a "view" into another Playfield at
 * a given x,y offset.  In some respects it is similar to a Cursor.
 *
 * Unlike a regular Playfield, the extent of an OverlayPlayfield
 * is fixed.  Attempts to access outside the bounds will return
 * the default element always.
 *
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class OverlayPlayfield<E extends Element> implements Playfield<E> {
    private Playfield<E> p;
    private IntegerElement offsetX = IntegerElement.ZERO;
    private IntegerElement offsetY = IntegerElement.ZERO;
    private IntegerElement width = IntegerElement.ZERO;
    private IntegerElement height = IntegerElement.ZERO;

    public OverlayPlayfield(Playfield<E> p) {
        this.p = p;
    }

    public OverlayPlayfield(Playfield<E> p, IntegerElement x, IntegerElement y, IntegerElement width, IntegerElement height) {
        this.p = p;
        this.offsetX = x;
        this.offsetY = y;
        this.width = width;
        this.height = height;
    }

    public void setPlayfield(Playfield<E> p) {
        this.p = p;
    }

    public void setOffsetX(IntegerElement x) {
        this.offsetX = x;
    }

    public void setOffsetY(IntegerElement y) {
        this.offsetY = y;
    }

    public void setWidth(IntegerElement width) {
        this.width = width;
    }

    public void setHeight(IntegerElement height) {
        this.height = height;
    }

    public Playfield<E> getPlayfield() {
        return p;
    }

    public IntegerElement getOffsetX() {
        return offsetX;
    }

    public IntegerElement getOffsetY() {
        return offsetY;
    }

    public IntegerElement getWidth() {
        return width;
    }

    public IntegerElement getHeight() {
        return height;
    }

    public void set(IntegerElement x, IntegerElement y, E e) {
        if (x.compareTo(IntegerElement.ZERO) < 0 ||
            x.compareTo(width) >= 0 ||
            y.compareTo(IntegerElement.ZERO) < 0 ||
            y.compareTo(height) >= 0) {
            return;
        }
        p.set(x.add(offsetX), y.add(offsetY), e);
    }

    public E get(IntegerElement x, IntegerElement y) {
        if (x.compareTo(IntegerElement.ZERO) < 0 ||
            x.compareTo(width) >= 0 ||
            y.compareTo(IntegerElement.ZERO) < 0 ||
            y.compareTo(height) >= 0) {
            return p.getDefault();
        }
        return p.get(x.add(offsetX), y.add(offsetY));
    }

    public E getDefault() {
        return p.getDefault();
    }

    /*
     * Note that clone() only clones the OverlayPlayfield,
     * *not* the underlying Playfield into which it views.
     */
    public OverlayPlayfield<E> clone() {
        OverlayPlayfield<E> c = new OverlayPlayfield<E>(
            p, offsetX, offsetY, width, height
        );
        return c;
    }

    public IntegerElement getMinX() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMinY() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMaxX() {
        return width.subtract(IntegerElement.ONE);
    }

    public IntegerElement getMaxY() {
        return height.subtract(IntegerElement.ONE);
    }

    public int numCursors() {
        return 0;
    }

    public Cursor getCursor(int index) {
        return null;
    }
}
