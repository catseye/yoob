/*
 * A Matcher defines how two Elements match in a pattern-
 * matching or unification scenario.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public interface Matcher<E extends Element, F extends Element> {
    boolean match(E sought, F candidate);
}
