/*
 * An IntegerElement is an unbounded integer that can be stored in
 * Playfields, Stacks, and Tapes.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.math.BigInteger;

public class IntegerElement extends BigInteger implements Element {
    public static IntegerElement ZERO = new IntegerElement(0);
    public static IntegerElement ONE = new IntegerElement(1);

    public IntegerElement(int initial) {
        super(new Integer(initial).toString());
    }

    public IntegerElement(BigInteger initial) {
        super(initial.toString());
    }

    public IntegerElement(char c) {
        super(new Character(c).toString());
    }

    public IntegerElement(String s) {
        super(s);
    }

    public String getName() {
        return toString();
    }

    public boolean equals(Element e) {
        if (e instanceof BigInteger) {
            return super.equals(e);
        }
        return false;
    }

    public IntegerElement fromChar(char c) {
        return new IntegerElement(c);
    }

    public IntegerElement negate() {
        return new IntegerElement(super.negate());
    }

    public IntegerElement add(IntegerElement o) {
        return new IntegerElement(super.add(o));
    }

    public IntegerElement subtract(IntegerElement o) {
        return new IntegerElement(super.subtract(o));
    }

    public IntegerElement multiply(IntegerElement o) {
        return new IntegerElement(super.multiply(o));
    }

    public IntegerElement divide(IntegerElement o) {
        return new IntegerElement(super.divide(o));
    }

    public IntegerElement succ() {
        return add(ONE);
    }

    public IntegerElement pred() {
        return subtract(ONE);
    }
  
    public int compareTo(int other) {
        return compareTo(new IntegerElement(other));
    }

    public boolean isZero() {
        return compareTo(ZERO) == 0;
    }
}
