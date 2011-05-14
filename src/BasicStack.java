/*
 * A BasicStack is yoob's stock implementation of a stack.
 * To ease the display and editing of stacks at debug-time,
 * stacks are built on tapes.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.HashMap;
import java.util.Map;

public class BasicStack<E extends Element> extends BasicTape<E> {
    public BasicStack(E def) {
        super(def);
    }

    public void push(E e) {
        Head<E> h = getHead(0);
        h.write(e);
        h.move(1);
    }

    public E pop() {
        Head<E> h = getHead(0);
        if (h.getPos().isZero()) {
            return def;
        }
        h.move(-1);
        return h.read();
    }

    public boolean isEmpty() {
        return getHead(0).getPos().isZero();
    }

    public IntegerElement getMax() {
        return getHead(0).getPos().pred();
    }

    public BasicStack<E> clone() {
        BasicStack<E> c = new BasicStack<E>(def);
        c.store = new HashMap<IntegerElement, E>((Map<IntegerElement, E>)this.store);
        c.min = this.min;
        c.max = this.max;
        c.head = this.head.clone();
        c.head.setTape(c);
        return c;
    }

}
