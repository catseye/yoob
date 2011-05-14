/*
 * A State is the state of a program at some given point.
 * It also encapsulates all the rules for progressing to the next
 * state in the program; however it is the Language object which
 * describes the static properties of the language.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.List;
import tc.catseye.yoob.Error; // not Java's Error...

public interface State {
    Language     getLanguage();
    State        clone();
    List<Error>  step(World w);
    Playfield    getPlayfield(int index);
    Tape         getTape(int index);
    String       getProgramText();
    List<Error>  setProgramText(String text);
    int          getProgramPosition();
    View         getPlayfieldView(int index);
    View         getTapeView(int index);
    /*
     * The code of State.step() makes this return true, and returns, when it
     * wants to indicate that the program has halted.  The intended effect is
     * that step is never called again on this State.
     */
    boolean      hasHalted();
    /*
     * The code of State.step() makes this return true, and returns, when it
     * wants to consume an Element given as input, but input() returned null,
     * indicating no such input was ready yet.  The intended effect is
     * that step is not called again until more input is available.
     */
    boolean      needsInput();
    String       exportToText();
    void         setOption(String name, boolean value);
}

