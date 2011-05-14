/*
 * A World represents the "outside world" that a State can interact
 * with.  This currently includes notions of input and output.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

public interface World {
    /*
     * The code of State.step() calls this when it wants to produce an
     * Element as output.
     */
    void output(Element e);

    /*
     * The code of State.step() calls this when it wants to consume a
     * CharacterElement given as input.  If no input CharacterElement
     * is ready, this will return null, in which case, the State can
     * set needsInput() on itself and return.
     */
    CharacterElement inputCharacter();
}
