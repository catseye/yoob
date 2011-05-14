/*
 * An Int32Element is a bounded (mod 2^31) signed integer that can
 * be stored in Playfields, Stacks, and Tapes.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class Int32Element implements Element {
    final public static Int32Element ZERO = new Int32Element(0);
    final public static Int32Element ONE = new Int32Element(1);

    final protected int value;

    public Int32Element(int value) {
        this.value = value;
    }

    public String getName() {
        return new Integer(value).toString();
    }

    public boolean equals(Element e) {
        if (e instanceof Int32Element) {
            return this.value == ((Int32Element)e).value;
        }
        return false;
    }

    public Int32Element fromChar(char c) {
        return new Int32Element((int)c);
    }

    public char toChar() {
        return (char)value;
    }

    public int getValue() {
        return value;
    }

    public Int32Element add(Int32Element other) {
        return new Int32Element(value + other.value);
    }

    public Int32Element subtract(Int32Element other) {
        return new Int32Element(value - other.value);
    }

    public Int32Element multiply(Int32Element other) {
        return new Int32Element(value * other.value);
    }

    public Int32Element divide(Int32Element other) {
        return new Int32Element(value / other.value);
    }

    public Int32Element modulo(Int32Element other) {
        return new Int32Element(value % other.value);
    }

    public Int32Element succ() {
        return new Int32Element(value + 1);
    }

    public Int32Element pred() {
        return new Int32Element(value - 1);
    }

    public boolean isZero() {
        return (value == 0);
    }
}

