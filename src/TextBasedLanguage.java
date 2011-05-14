/*
 * A TextBasedLanguage is an abstract convenience superclass for
 * esolangs whose program is most easily described as a text file.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.List;
import java.util.ArrayList;
import tc.catseye.yoob.Error; // not Java's Error...

public abstract class TextBasedLanguage<S extends State> implements Language {
    abstract public String getName();
  
    // Typically:
    public int numPlayfields() {
        return 0;
    }

    public int numTapes() {
        return 1;
    }

    public boolean hasProgramText() {
        return true;
    }

    public boolean hasInput() {
        return true;
    }

    public boolean hasOutput() {
        return true;
    }

    abstract public List<String> exampleProgramNames();
    abstract public S loadExampleProgram(int index);
    abstract public S importFromText(String text);

    public S importFromText(String[] lines) {
        StringBuffer result = new StringBuffer();
        if (lines.length > 0) {
            result.append(lines[0]);
            for (int i=1; i<lines.length; i++) {
                result.append("\n");
                result.append(lines[i]);
            }
        }
        return importFromText(result.toString());
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    abstract public String[][] getProperties();
}

