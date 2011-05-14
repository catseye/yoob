/*
 * A BasicHead is yoob's stock implementation of a Head.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class BasicHead<E extends Element> implements Head<E> {
    private Tape<E> tape;
    private IntegerElement pos;

    public BasicHead(Tape<E> tape) {
        pos = IntegerElement.ZERO;
        this.tape = tape;
    }

    public BasicHead(Tape<E> tape, IntegerElement pos) {
        this.pos = pos;
        this.tape = tape;
    }

    public void setTape(Tape<E> tape) {
        this.tape = tape;
    }

    public Tape<E> getTape() {
        return tape;
    }

    public BasicHead<E> clone() {
        return new BasicHead<E>(tape, pos);
    }

    public E read() {
        return tape.read(getPos());
    }

    public void write(E e) {
        tape.write(getPos(), e);
    }

    public IntegerElement getPos() {
        return pos;
    }

    public void setPos(IntegerElement pos) {
        this.pos = pos;
    }

    public void setPos(int pos) {
        this.pos = new IntegerElement(pos);
    }

    public void move(IntegerElement delta) {
        pos = pos.add(delta);
    }

    public void move(int delta) {
        move(new IntegerElement(delta));
    }

    public boolean isAt(IntegerElement pos) {
        return getPos().compareTo(pos) == 0;
    }

    public boolean isAt(int pos) {
        return isAt(new IntegerElement(pos));
    }
}
