/*
 * A Tape is one of a few standard data structures provided by yoob.
 * It is a (potentially mutable) one-dimensional sequence of cells
 * addressed by a head.  You know, like a Turing machine?
 * Each cell may contain an Element (which can be any value that
 * implements the Element interface.)
 *
 * The source code in this file has been placed into the public domain.
 */ 
package tc.catseye.yoob;

public interface Tape<E extends Element> {
    Tape            clone();
    E               read(IntegerElement pos);
    void            write(IntegerElement pos, E e);
    IntegerElement  getMin();
    /*
     * Note that this can be equal to getMin()-1, indicating
     * there is nothing taking up space in this tape.
     */
    IntegerElement  getMax();
    int             numHeads();
    Head            getHead(int index);
}
