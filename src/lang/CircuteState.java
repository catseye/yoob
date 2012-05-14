/*
 * A CircuteState implement the semantics of the Circute CA.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.circute;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class Circute implements Language {
    public String getName() {
        return "Circute";
    }

    public int numPlayfields() {
        return 1;
    }

    public int numTapes() {
        return 0;
    }

    public boolean hasProgramText() {
        return false;
    }

    public boolean hasInput() {
        return false;
    }

    public boolean hasOutput() {
        return false;
    }

    public List<String> exampleProgramNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("AND gate");
        names.add("OR gate");
        names.add("One-fire switch (OSC->off)");
        names.add("Tiny oscillator");
        names.add("Fast oscillator");
        names.add("Tiny inverter");
        return names;
    }

    public CircuteState loadExampleProgram(int index) {
        // All examples from: (but possibly modified slightly)
        // http://www.esolangs.org/wiki/Circute
        // Authors unknown.  From the esowiki, thus in the public domain.
        String[][] program = {
          {
            "#==     ==#",
            "  =     =",
            "  ===N===",
            "     =",
            "     =",
            "   =====",
            "   =   =",
            "   =#N#=",
            "     =",
          },
          {
            "#==     ==",
            "  =     =",
            "===== =====",
            "=   = =   =",
            "==N== ==N==",
            "  =     =",
            "  ==#N#==",
            "     =",
            "     =",
          },
          {
            "  ===#N=========",
            "  =     =   =",
            "==N== ===== =",
            "=   = =   = =",
            "===== ==N== =",
            "  =     =   =",
            "  =     =====",
            "  =",
            "  =============#",
          },
          {
            "#N=",
            " ======================",
          },
          {
            " =N#",
            "#N=",
            "  ======================",
          },
          {
            "===========#",
            "=",
            "=N#",
            " =",
            " ===========",
          }
        };
        CircuteState s = new CircuteState();
        s.playfield.load(program[index]);
        return s;
    }

    public CircuteState importFromText(String text) {
        CircuteState s = new CircuteState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Chris Pressey"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "This hand-implementation is based on the ALPACA description, but is not " +
         "compiled from it."}
    };

    public String[][] getProperties() {
        return properties;
    }

}

class CircutePlayfield extends CellularAutomatonPlayfield<CharacterElement> {
    static final char WIRE  = '=';
    static final char TAIL  = '-';
    static final char SPARK = '#';
    static final char NAND  = 'N';
    static final char SPACE = ' ';

    public CircutePlayfield() {
        super(new CharacterElement(SPACE));
    }

    public CircutePlayfield clone() {
        CircutePlayfield c = new CircutePlayfield();
        c.store = new HashMap<Position, CharacterElement>(
          (Map<Position, CharacterElement>)this.store
        );
        c.copyBackingStoreFrom(this);
        return c;
    }

    public CharacterElement applyRules(IntegerElement x, IntegerElement y, CharacterElement elem) {
        if (elem.getChar() == TAIL) {
            return new CharacterElement(WIRE);
        } else if (elem.getChar() == WIRE) {
            boolean adjacentSpark = (
                get(x.succ(), y).getChar() == SPARK ||
                get(x.pred(), y).getChar() == SPARK ||
                get(x, y.succ()).getChar() == SPARK ||
                get(x, y.pred()).getChar() == SPARK
            );
            boolean activeNANDBelow = (
                get(x, y.succ()).getChar() == NAND &&
                (get(x.pred(), y.succ()).getChar() == WIRE ||
                 get(x.succ(), y.succ()).getChar() == WIRE)
            );
            boolean activeNANDAbove = (
                get(x, y.pred()).getChar() == NAND &&
                (get(x.pred(), y.pred()).getChar() == WIRE ||
                 get(x.succ(), y.pred()).getChar() == WIRE)
            );
            if (adjacentSpark || activeNANDBelow || activeNANDAbove)
                return new CharacterElement(SPARK);
        } else if (elem.getChar() == SPARK) {
            boolean adjacentTail = (
                get(x.succ(), y).getChar() == TAIL ||
                get(x.pred(), y).getChar() == TAIL ||
                get(x, y.succ()).getChar() == TAIL ||
                get(x, y.pred()).getChar() == TAIL
            );
            boolean inactiveNANDBelow = (
                get(x, y.succ()).getChar() == NAND &&
                get(x.pred(), y.succ()).getChar() == SPARK &&
                get(x.succ(), y.succ()).getChar() == SPARK
            );
            boolean inactiveNANDAbove = (
                get(x, y.pred()).getChar() == NAND &&
                get(x.pred(), y.pred()).getChar() == SPARK &&
                get(x.succ(), y.pred()).getChar() == SPARK
            );
            if (adjacentTail || inactiveNANDBelow || inactiveNANDAbove)
                return new CharacterElement(TAIL);
        }
        return elem.getChar() == ' ' ? null : elem;
    }
}

public class CircuteState implements State {
    protected CircutePlayfield playfield;
    protected BasicPlayfieldView pfView;
    private static final Circute language = new Circute();

    public CircuteState() {
        playfield = new CircutePlayfield();
        pfView = new BasicPlayfieldView();
    }

    public Language getLanguage() {
        return language;
    }
    
    public CircuteState clone() {
        CircuteState c = new CircuteState();
        c.playfield = this.playfield.clone();
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        CircutePlayfield nu = new CircutePlayfield();
        playfield.step(nu);
        playfield = nu;
        return errors;
    }

    public Playfield getPlayfield(int index) {
        if (index == 0)
            return playfield;
        return null;
    }

    public Tape getTape(int index) {
        return null;
    }

    public String getProgramText() {
        return "";
    }

    public int getProgramPosition() {
        return 0;
    }

    public List<Error> setProgramText(String text) {
        ArrayList<Error> errors = new ArrayList<Error>();
        return errors;
    }

    public View getPlayfieldView(int index) {
        return pfView;
    }

    public View getTapeView(int index) {
        return null;
    }

    public String exportToText() {
        return playfield.dump();
    }

    public boolean hasHalted() {
        return false;
    }

    public boolean needsInput() {
        return false;
    }

    public void setOption(String name, boolean value) {
    }
}
