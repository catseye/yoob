/*
 * A BasicPlayfield is yoob's stock implementation of a playfield.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class BasicPlayfield<E extends Element> implements Playfield<E> {
    protected HashMap<Position, E> store;
    private IntegerElement minX, minY, maxX, maxY;
    private E def;

    public BasicPlayfield(E def) {
        this.def = def;
        clear();
    }

    public BasicPlayfield<E> clone() {
        BasicPlayfield<E> c = new BasicPlayfield<E>(def);
        c.copyBackingStoreFrom(this);
        return c;
    }

    // provided to make implementing clone() less burdensome.
    protected void copyBackingStoreFrom(BasicPlayfield<E> other) {
        store = new HashMap<Position, E>((Map<Position, E>)other.store);
        minX = other.getMinX();
        minY = other.getMinY();
        maxX = other.getMaxX();
        maxY = other.getMaxY();
    }

    public void clear() {
        store = new HashMap<Position, E>();
        minX = IntegerElement.ZERO;
        minY = IntegerElement.ZERO;
        maxX = IntegerElement.ONE.negate();
        maxY = IntegerElement.ONE.negate();
    }

    public void set(IntegerElement x, IntegerElement y, E e) {
        Position pos = new Position(x, y);
        store.put(pos, e);
        //System.out.printf("put %s at (%s,%s) --> %s\n", e.getName(), x, y, e2.getName());
        //System.out.printf("set @ (%s,%s)\n  lower bounds were (%s,%s)\n", x, y, min_x, min_y);
        if (x.compareTo(minX) <= 0) {
            minX = x;
        }
        if (x.compareTo(maxX) >= 0) {
            maxX = x;
        }
        if (y.compareTo(minY) <= 0) {
            minY = y;
        }
        if (y.compareTo(maxY) >= 0) {
            maxY = y;
        }
        //System.out.printf("  new lower bounds are (%s,%s)\n", min_x, min_y);
    }

    public void set(int x, int y, E e) {
        set(new IntegerElement(x), new IntegerElement(y), e);
    }

    public E get(IntegerElement x, IntegerElement y) {
        E result = store.get(new Position(x, y));
        return (result == null) ? def : result;
    }

    public E get(int x, int y) {
        return get(new IntegerElement(x), new IntegerElement(y));
    }

    public E getDefault() {
        return def;
    }

    public IntegerElement getMinX() {
        return minX;
    }

    public IntegerElement getMaxX() {
        return maxX;
    }

    public IntegerElement getMinY() {
        return minY;
    }

    public IntegerElement getMaxY() {
        return maxY;
    }

    public int numCursors() {
        return 0;
    }

    public Cursor<E> getCursor(int index) {
        return null;
    }

    // following methods are over and above Playfield interface

    public void load(String[] lines) {
        for (int y = 0; y < lines.length; y++) {
            String r = lines[y];
            for (int x = 0; x < r.length(); x++) {
                loadChar(x, y, r.charAt(x));
            }
        }
    }

    // TODO this should take a Codec<E>
    public void loadChar(int x, int y, char c) {
        Element e = def.fromChar(c);
        set(x, y, (E)e);
    }

    /*
     * Return true iff the given cursor has "fallen off the
     * edge of the world"; it is positioned and pointing
     * in such a way that it will not encounter any non-
     * default-valued element in the Playfield, in its path.
     * XXX this should be in BasicCursor now
     */
    public boolean hasFallenOffEdge(BasicCursor c) {
        return (c.getX().compareTo(maxX) > 0 && c.getDeltaX().compareTo(IntegerElement.ZERO) > 0) ||
               (c.getX().compareTo(minX) < 0 && c.getDeltaX().compareTo(IntegerElement.ZERO) < 0) ||
               (c.getY().compareTo(maxY) > 0 && c.getDeltaY().compareTo(IntegerElement.ZERO) > 0) ||
               (c.getY().compareTo(minY) < 0 && c.getDeltaY().compareTo(IntegerElement.ZERO) < 0);
    }

    /*
     * Given a range inside this BasicPlayfield, return a new BasicPlayfield
     * from that range.
     */
    public BasicPlayfield<E> extract(IntegerElement x, IntegerElement y, IntegerElement width, IntegerElement height) {
        BasicPlayfield<E> c = new BasicPlayfield<E>(def);
        IntegerElement cx, cy, dx, dy;
        for (cx = x, dx = IntegerElement.ZERO;
             cx.compareTo(x.add(width)) < 0;
             cx = cx.succ(), dx = dx.succ()) {
            for (cy = y, dy = IntegerElement.ZERO;
                 cy.compareTo(y.add(height)) < 0;
                 cy = cy.succ(), dy = dy.succ()) {
                c.set(dx, dy, get(cx, cy));
            }
        }
        // TODO: clone cursors?
        return c;
    }

    public String dump() {
        IntegerElement min_x = getMinX();
        IntegerElement min_y = getMinY();
        IntegerElement max_x = getMaxX();
        IntegerElement max_y = getMaxY();
        IntegerElement x = min_x;
        IntegerElement y = min_y;
        StringBuffer buf = new StringBuffer();
        int blanks;

        while (y.compareTo(max_y) <= 0) {
            x = min_x;
            blanks = 0;
            while (x.compareTo(max_x) <= 0) {
                E e = get(x, y);
                if (e.equals(def)) {
                    blanks++;
                } else {
                    while (blanks > 0) {
                        buf.append(dumpElement(def));
                        blanks--;
                    }
                    buf.append(dumpElement(e));
                }
                x = x.succ();
            }
            y = y.succ();
            buf.append("\n");
        }

        return buf.toString();
    }

    public String dumpElement(E e) {
        return e.getName();
    }
}
