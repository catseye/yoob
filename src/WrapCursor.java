/*
 * A WrapCursor is a BasicCursor that wraps around at the
 * edge of the playfield.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class WrapCursor<E extends Element> extends BasicCursor<E> {
    public WrapCursor(Playfield<E> p) {
        super(p);
    }

    public WrapCursor(Playfield<E> p, IntegerElement x, IntegerElement y, IntegerElement dx, IntegerElement dy) {
        super(p, x, y, dx, dy);
    }

    public WrapCursor<E> clone() {
        return new WrapCursor<E>(getPlayfield(), x, y, dx, dy);
    }

    public void move(IntegerElement dx, IntegerElement dy) {
        Playfield<E> pf = getPlayfield();
        IntegerElement width = pf.getMaxX().subtract(pf.getMinX()).succ();
        IntegerElement height = pf.getMaxY().subtract(pf.getMinY()).succ();

        x = x.add(new IntegerElement(dx));
        if (x.compareTo(pf.getMinX()) < 0) {
            x = x.add(width);
        } else if (x.compareTo(pf.getMaxX()) > 0) {
            x = x.subtract(width);
        }

        y = y.add(new IntegerElement(dy));
        if (y.compareTo(pf.getMinY()) < 0) {
            y = y.add(height);
        } else if (y.compareTo(pf.getMaxY()) > 0) {
            y = y.subtract(height);
        }
    }
}
