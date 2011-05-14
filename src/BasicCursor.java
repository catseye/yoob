/*
 * A BasicCursor is yoob's stock implementation of a Cursor.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class BasicCursor<E extends Element> implements Cursor<E> {
    private Playfield<E> p;
    protected IntegerElement x;
    protected IntegerElement y;
    protected IntegerElement dx;
    protected IntegerElement dy;

    public BasicCursor(Playfield<E> p) {
        this.p = p;
        x = IntegerElement.ZERO;
        y = IntegerElement.ZERO;
        dx = IntegerElement.ZERO;
        dy = IntegerElement.ZERO;
    }

    public BasicCursor(Playfield<E> p, IntegerElement x, IntegerElement y, IntegerElement dx, IntegerElement dy) {
        this.p = p;
        this.x = x;
        this.y = y;
        this.dx = dx;
        this.dy = dy;
    }

    public BasicCursor<E> clone() {
        return new BasicCursor<E>(p, x, y, dx, dy);
    }

    public void setPlayfield(Playfield<E> p) {
        this.p = p;
    }

    public Playfield<E> getPlayfield() {
        return p;
    }

    public void set(E e) {
        p.set(x, y, e);
    }

    public E get() {
        return p.get(x, y);
    }

    public IntegerElement getX() {
        return x;
    }

    public IntegerElement getY() {
        return y;
    }

    public void setX(IntegerElement x) {
        this.x = x;
    }

    public void setX(int x) {
        this.x = new IntegerElement(x);
    }

    public void setY(IntegerElement y) {
        this.y = y;
    }

    public void setY(int y) {
        this.y = new IntegerElement(y);
    }

    public void setDeltaX(IntegerElement dx) {
        this.dx = dx;
    }

    public void setDeltaX(int dx) {
        this.dx = new IntegerElement(dx);
    }

    public void setDeltaY(IntegerElement dy) {
        this.dy = dy;
    }

    public void setDeltaY(int dy) {
        this.dy = new IntegerElement(dy);
    }

    public void setDelta(IntegerElement dx, IntegerElement dy) {
        setDeltaX(dx);
        setDeltaY(dy);
    }

    public void setDelta(int dx, int dy) {
        setDeltaX(dx);
        setDeltaY(dy);
    }

    public IntegerElement getDeltaX() {
        return dx;
    }

    public IntegerElement getDeltaY() {
        return dy;
    }

    public void move(IntegerElement dx, IntegerElement dy) {
        x = x.add(new IntegerElement(dx));
        y = y.add(new IntegerElement(dy));
    }

    public void move(int dx, int dy) {
        move(new IntegerElement(dx), new IntegerElement(dy));
    }

    public void advance() {
        move(dx, dy);
    }

    public void advance(IntegerElement factor) {
        move(dx.multiply(factor), dy.multiply(factor));
    }

    public void advance(int factor) {
        advance(new IntegerElement(factor));
    }

    public void reflect() {
        dx = dx.negate();
        dy = dy.negate();
    }

    /* This only works for 45-degree increments and "pseudo unit vector" deltas. */
    public void rotate(int degrees) {
        int[][] table = {
          {0, -1},
          {1, -1},
          {1, 0},
          {1, 1},
          {0, 1},
          {-1, 1},
          {-1, 0},
          {-1, -1}
        };
        int index = -1;
        for (int i = 0; i <= 7; i++) {
            int px = table[i][0];
            int py = table[i][1];
            if (dx.intValue() == px && dy.intValue() == py) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return; // TODO: complain
        }
        int nuIndex = (index + (int)(degrees / 45));
        if (nuIndex < 0) nuIndex += 8;
        nuIndex %= 8;
        int nuDx = table[nuIndex][0];
        int nuDy = table[nuIndex][1];
        dx = new IntegerElement(nuDx);
        dy = new IntegerElement(nuDy);
    }

    public boolean isHeaded(int dx, int dy) {
        return getDeltaX().intValue() == dx && getDeltaY().intValue() == dy;
    }

}
