/*
 * An Element is an interface that any value may implement to
 * declare that it may be stored in yoob data structures
 * (primarily Playfield and Tape.) and may be depicted by yoob
 * using a View.  (Distinct values should have distinct depictions.)
 *
 * Elements are generally expected to be immutable; i.e. the same
 * Element may be stored at multiple locations in a Playfield or Tape,
 * and will not be "updated" at a location, but rather replaced by
 * a different Element.  For this reason, objects that implement the
 * Element interface are encouraged to implement a Featherweight
 * design pattern, for efficiency.
 *
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public interface Element {
    String      getName();
    boolean     equals(Element e);
    /* virtual static: */
    Element     fromChar(char c);
}
