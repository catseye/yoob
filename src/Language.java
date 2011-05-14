/*
 * A Language describes the static properties of a particular
 * language, and acts as a factory for States of programs in that
 * language.  However, it does not define the rules for progressing
 * to the next state in the program; that is encapsulated in State.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.List;
import tc.catseye.yoob.Error; // not Java's Error...

public interface Language {
    String       getName();         // for populating the menu, etc
    int          numPlayfields();
    int          numTapes();
    boolean      hasProgramText();
    boolean      hasInput();
    boolean      hasOutput();
    List<String> exampleProgramNames();
    State        loadExampleProgram(int index);
    State        importFromText(String text);
    List<String> getAvailableOptionNames();
    String[][]   getProperties();
}

