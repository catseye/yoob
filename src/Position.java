/*
 * A Position is an immutable pair of IntegerElements, used as
 * a key in maps implementing backing stores for Playfields.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class Position {
    private final IntegerElement x, y;

    public Position(IntegerElement x, IntegerElement y) {
        this.x = (x == null) ? IntegerElement.ZERO : x;
        this.y = (y == null) ? IntegerElement.ZERO : y;
    }

    public Position(int x, int y) {
        this.x = new IntegerElement(x);
        this.y = new IntegerElement(y);
    }

    public int hashCode() {
        return 31 * x.hashCode() + y.hashCode();
    }

    public boolean equals(Object other) {
        if (other instanceof Position) {
            Position o = (Position)other;
            if (x.equals(o.getX()) && y.equals(o.getY()))
                return true;
        }
        return false;
    }

    public IntegerElement getX() {
        return x;
    }

    public IntegerElement getY() {
        return y;
    }
    
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
