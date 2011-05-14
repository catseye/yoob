/*
 * A Playfield is one of a few standard data structures provided by yoob.
 * It is a (potentially mutable) two-dimensional grid of cells.  Each cell may
 * contain an Element (which can be any value that implements the Element
 * interface.)
 * The source code in this file has been placed into the public domain.
 */ 
package tc.catseye.yoob;

public interface Playfield<E extends Element> {
    void           set(IntegerElement x, IntegerElement y, E e);
    E              get(IntegerElement x, IntegerElement y);
    E              getDefault();
    Playfield<E>   clone();
    IntegerElement getMinX();
    /*
     * Note that this can be equal to getMinX()-1, indicating
     * there is nothing taking up horizontal space in playfield.
     */
    IntegerElement getMaxX();
    IntegerElement getMinY();
    /*
     * Note that this can be equal to getMinY()-1, indicating
     * there is nothing taking up vertical space in playfield.
     */
    IntegerElement getMaxY();
    int            numCursors();
    Cursor         getCursor(int index);
}
