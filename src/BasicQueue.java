/*
 * A BasicQueue is yoob's stock implementation of a queue.
 * To ease the display and editing of queues at debug-time,
 * queues are built on tapes.
 * Actually you get a full deque with this.  If you want a
 * queue, only use the enqueue and dequeue methods.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.HashMap;
import java.util.Map;

public class BasicQueue<E extends Element> extends BasicTape<E> {
    protected BasicHead<E> tail;

    public BasicQueue(E def) {
        super(def);
        tail = new BasicHead<E>(this, IntegerElement.ONE.negate());
    }

    public void enqueue(E e) {
        Head<E> tail = getHead(1);
        tail.move(1);
        tail.write(e);
    }

    public void enqueueAtHead(E e) {
        Head<E> head = getHead(0);
        head.move(-1);
        head.write(e);
    }

    public E dequeue() {
        if (isEmpty()) {
            return def;
        }
        Head<E> head = getHead(0);
        E e = head.read();
        head.move(1);
        return e;
    }

    public E dequeueAtTail() {
        if (isEmpty()) {
            return def;
        }
        Head<E> tail = getHead(1);
        E e = tail.read();
        tail.move(-1);
        return e;
    }

    public E peek() {
        if (isEmpty()) {
            return def;
        }
        return getHead(0).read();
    }

    public E peekAtTail() {
        if (isEmpty()) {
            return def;
        }
        return getHead(1).read();
    }

    public int numHeads() {
        return 2;
    }

    public BasicHead<E> getHead(int index) {
        if (index == 0)
            return head;
        else if (index == 1)
            return tail;
        return null;
    }

    public boolean isEmpty() {
        return getHead(1).getPos().compareTo(getHead(0).getPos()) < 0;
    }

    public IntegerElement getMin() {
        return getHead(0).getPos();
    }

    public IntegerElement getMax() {
        return getHead(1).getPos();
    }

    public BasicQueue<E> clone() {
        BasicQueue<E> c = new BasicQueue<E>(def);
        c.store = new HashMap<IntegerElement, E>((Map<IntegerElement, E>)this.store);
        c.min = this.min;
        c.max = this.max;
        c.head = this.head.clone();
        c.tail= this.tail.clone();
        c.head.setTape(c);
        c.tail.setTape(c);
        return c;
    }
}
