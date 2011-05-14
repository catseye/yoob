/*
 * A Cursor refers to a location in a Playfield.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public interface Cursor<E extends Element> {
    void           setPlayfield(Playfield<E> p);
    Playfield<E>   getPlayfield();
    void           set(E e);
    E              get();
    IntegerElement getX();
    IntegerElement getY();
    void           setX(IntegerElement x);
    void           setY(IntegerElement y);
    Cursor<E>      clone();
}
