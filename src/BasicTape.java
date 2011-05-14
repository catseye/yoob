/*
 * A BasicTape is yoob's stock implementation of a tape.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.HashMap;
import java.util.Map;

public class BasicTape<E extends Element> implements Tape<E> {
    protected HashMap<IntegerElement, E> store;
    protected IntegerElement min, max;
    protected BasicHead<E> head;
    protected E def;

    public BasicTape(E def) {
        store = new HashMap<IntegerElement, E>();
        min = IntegerElement.ZERO;
        max = IntegerElement.ONE.negate();
        head = new BasicHead<E>(this, IntegerElement.ZERO);
        this.def = def;
    }

    public BasicTape<E> clone() {
        BasicTape<E> c = new BasicTape<E>(def);
        c.store = new HashMap<IntegerElement, E>((Map<IntegerElement, E>)this.store);
        c.min = this.min;
        c.max = this.max;
        c.head = this.head.clone();
        c.head.setTape(c);
        return c;
    }

    public E read(IntegerElement pos) {
        E e = store.get(pos);
        return e == null ? def : e;
    }

    public E read(int pos) {
        return read(new IntegerElement(pos));
    }

    public void write(IntegerElement pos, E e) {
        store.put(pos, e);
        if (pos.compareTo(min) < 0) {
            min = pos;
        }
        if (pos.compareTo(max) > 0) {
            max = pos;
        }
    }

    public void write(int pos, E e) {
        write(new IntegerElement(pos), e);
    }

    public IntegerElement getMin() {
        return min;
    }

    public IntegerElement getMax() {
        return max;
    }

    public int numHeads() {
        return 1;
    }

    public BasicHead<E> getHead(int index) {
        if (index == 0)
            return head;
        return null;
    }

    public void dump() {
        IntegerElement i;
        System.out.println("+++ tape: " + this.toString());
        for (i = getMin(); i.compareTo(getMax()) <= 0; i = i.succ()) {
            System.out.println("+ " + i + " -> " + read(i).getName());
        }
        System.out.println("++++++++");
    }
}
