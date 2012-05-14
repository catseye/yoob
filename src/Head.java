/*
 * A Head represents a position on a Tape.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public interface Head<E extends Element> {
    void           setTape(Tape<E> t);
    Tape<E>        getTape();
    Head           clone();
    E              read();
    void           write(E e);
    IntegerElement getPos();
    void           setPos(IntegerElement pos);
    void           move(IntegerElement delta);
    void           move(int delta);
}
