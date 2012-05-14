/*
 * A ByteElement is an bounded (mod 256) non-negative integer that can
 * be stored in Playfields, Stacks, and Tapes.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class ByteElement implements Element {
    final protected short value; // because Java's bytes are signed
    public static final ByteElement ZERO = new ByteElement(0);
    public static final ByteElement ONE = new ByteElement(1);

    public ByteElement(int value) {
        while (value < 0) value += 256;
        this.value = (short)(value % 256);
    }

    public ByteElement(char value) {
        int v = (int)value;
        while (v < 0) v += 256;
        this.value = (short)(v % 256);
    }

    public String getName() {
        return new Short(value).toString();
    }

    public boolean equals(Element e) {
        if (e instanceof ByteElement) {
            return this.value == ((ByteElement)e).value;
        }
        return false;
    }

    public ByteElement fromChar(char c) {
        return new ByteElement(c);
    }

    public char toChar() {
        return (char)value;
    }

    public IntegerElement toIntegerElement() {
        return new IntegerElement(value);
    }

    public ByteElement succ() {
        return new ByteElement(value + 1);
    }

    public ByteElement pred() {
        return new ByteElement(value - 1);
    }

    public ByteElement add(ByteElement other) {
        return new ByteElement(value + other.value);
    }

    public ByteElement subtract(ByteElement other) {
        return new ByteElement(value - other.value);
    }

    public ByteElement multiply(ByteElement other) {
        return new ByteElement(value * other.value);
    }

    public boolean isZero() {
        return (value == 0);
    }
}
