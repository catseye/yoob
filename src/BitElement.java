/*
 * A BitElement is a value which is either 0 or 1 and can
 * be stored in Playfields, Stacks, and Tapes.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class BitElement implements Element {
    private final boolean value;
    public static final BitElement ZERO = new BitElement(false);
    public static final BitElement ONE = new BitElement(true);

    public BitElement(boolean value) {
        this.value = value;
    }
    
    public static BitElement create(boolean value) {
        return value ? ONE : ZERO;
    }

    public String getName() {
        return value ? "1" : "0";
    }

    public BitElement fromChar(char c) {
        return c == '1' ? ONE : ZERO;
    }

    public boolean equals(Element e) {
        if (e instanceof BitElement) {
            return this.value == ((BitElement)e).getBoolean();
        }
        return false;
    }

    public boolean getBoolean() {
        return value;
    }

    public boolean isZero() {
        return !value;
    }

    public BitElement invert() {
        return this == ONE ? ZERO : ONE;
    }
}
