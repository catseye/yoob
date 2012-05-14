/*
 * An EqualityMatcher is a Matcher that just checks for
 * equality.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public class EqualityMatcher<E extends Element> implements Matcher<E,E> {
    public boolean match(E sought, E candidate) {
        return candidate.equals(sought);
    }
}
