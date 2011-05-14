/*
 * A CharacterElement is a Java Character that can be used as an Element in
 * Playfields, Stacks, and Tapes.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class CharacterElement implements Element {
    private final Character c;

    public CharacterElement(char c) {
        this.c = new Character(c);
    }

    public CharacterElement(int initial) {
        this.c = new Character((char)initial);
    }

    public String getName() {
        return c.toString();
    }

    public boolean equals(Element e) {
        if (e instanceof CharacterElement) {
            return this.getName().equals(e.getName());
        }
        return false;
    }

    public CharacterElement fromChar(char o) {
        return new CharacterElement(o);
    }

    public char getChar() {
        return c.toString().charAt(0);
    }

    public boolean isDigit() {
        return Character.isDigit(c.charValue());
    }

    /*
     * Not well defined if isDigit() is false
     */
    public int digitValue() {
        return (int)c.charValue() - (int)'0';
    }
}
