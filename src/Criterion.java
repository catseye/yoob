/*
 * A Criterion encapsulates a predicate about elements.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public interface Criterion<E extends Element> {
    boolean qualifies(E e);
}
