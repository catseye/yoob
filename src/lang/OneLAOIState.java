/*
 * A OneLAOIState implements the semantics of 1L_AOI,
 * with an option for 1L_AOI_EU.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.onelaoi;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;


class OneLAOI implements Language {
    public String getName() {
        return "1L_AOI";
    }

    public int numPlayfields() {
        return 1;
    }

    public int numTapes() {
        return 1;
    }

    public boolean hasProgramText() {
        return false;
    }

    public boolean hasInput() {
        return true;
    }

    public boolean hasOutput() {
        return true;
    }

    public List<String> exampleProgramNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("output a bang (1L_AOI_EU)");
        return names;
    }

    public OneLAOIState loadExampleProgram(int index) {
        // All examples from: http://www.esolangs.org/wiki/1L_AOI
        // By Chris Pressey.  From the esowiki, thus in the public domain.
        String[][] program = {
          {
		"    +",
		" ++",
		"",
		"+      +",
		"",
		" +    +",
		"      +",
		"         +",
		"      +",
		"",
		"+        +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"      +",
		"",
		"",
		" +      +",
		"",
		"",
		" +  ++  +",
          },
        };
        OneLAOIState s = new OneLAOIState();
        s.playfield.load(program[index]);
        return s;
    }

    public OneLAOIState importFromText(String text) {
        OneLAOIState s = new OneLAOIState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("1L_AOI_EU");
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Tslil Clingman"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "This implementation follows the language described in the esowiki " +
         "article after January 2011.  The 1L_AOI_EU extension is available, " +
         "and the example programs need it."},
    };

    public String[][] getProperties() {
        return properties;
    }
}

public class OneLAOIState implements State {
    protected BasicTape<ByteElement> tape;
    protected CommonPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected boolean eu = false;
    private static final OneLAOI language = new OneLAOI();
  
    public OneLAOIState() {
        tape = new BasicTape<ByteElement>(new ByteElement(0));
        BasicHead head = tape.getHead(0);
        // In 1l_AOI, TL1 always has a non-zero value, which allows for a conditional turn 
        // to occur when the Memory Pointer is pointing to it.
        tape.write(IntegerElement.ONE, new ByteElement(1));
        // Tape head is initially on TL2.
        head.setPos(new IntegerElement(2));
        playfield = new CommonPlayfield();
        playfield.getCursor(0).setY(new IntegerElement(1));
        pfView = new BasicPlayfieldView();
        tapeView = new BasicTapeView();
    }

    public Language getLanguage() {
        return language;
    }
    
    public OneLAOIState clone() {
        OneLAOIState c = new OneLAOIState();
        c.playfield = this.playfield.clone();
        c.tape = this.tape.clone();
        c.halted = halted;
        c.needsInput = needsInput;
        c.eu = eu;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicHead<ByteElement> h = tape.getHead(0);
        ByteElement b = h.read();
        char instruction = ip.get().getChar();

        if (instruction == '+') {
            /* If the Command Pointer passes through a + sign then the following is evaluated: */
            if (ip.isHeaded(0, -1)) {
                // Up -- Increase MP Cell by one
                if (h.isAt(1)) {
                    if (!doIO(world)) return errors;
                } else {
                    h.write(b.succ());
                }
            } else if (ip.isHeaded(0, 1)) {
                // Down -- Move MP Right
                h.move(1);
            } else if (ip.isHeaded(1, 0)) {
                // Right -- Move MP Left
                h.move(-1);
            } else if (ip.isHeaded(-1, 0)) {
                // Left -- Decrease MP Cell by one
                if (h.isAt(1)) {
                    if (!doIO(world)) return errors;
                } else {
                    h.write(b.pred());
                }
            } else {
                // TODO: add error to errors
            }
        } else {
            // Anything else does nothing and can be used for comments. 
        }

        /*
         * If the Command Pointer passes by a <code>+</code> sign, the effect is determined as follows.
         * Normally, the Command Pointer will turn away from the <code>+</code>.
         * If however, the Command Pointer would have been turned left, and the
         *   the Memory Pointer cell is zero, no turn occurs and the Command Pointer proceeds straight.
         * (The <code>+</code> sign must be diagonally opposite the
         * point at which the CP is required to turn.)
         */

        // Check the two diagonally-in-front-of squares for +'s
        
        boolean rotateRight = false, rotateLeft = false;

        BasicCursor<CharacterElement> aheadLeft = ip.clone();
        aheadLeft.rotate(-45);
        aheadLeft.advance();
        if (aheadLeft.get().getChar() == '+') {
            rotateRight = true;
        }

        BasicCursor<CharacterElement> aheadRight = ip.clone();
        aheadRight.rotate(45);
        aheadRight.advance();
        if (aheadRight.get().getChar() == '+') {
            rotateLeft = true;
        }

        b = h.read();

        if (!eu) {
            /*
             * Here's where 1L_AOI and 1L_AOI_EU differ.  In 1L_AOI, deflection is
             * conditional, full stop.
             */
            rotateRight = rotateRight && (!b.isZero());
            rotateLeft = rotateLeft && (!b.isZero());
            if (rotateLeft && rotateRight) {
                ip.rotate(180);
            } else if (rotateRight) {
                ip.rotate(90);
            } else if (rotateLeft) {
                ip.rotate(-90);
            }
        } else {
            /*
             * In 1L_AOI_EU, deflection to the right(?) is conditional on non-zero,
             * deflection to the left(?) is conditional on zero.
             */
            rotateRight = rotateRight && b.isZero();
            rotateLeft = rotateLeft && (!b.isZero());
            if (rotateLeft && rotateRight) {
                ip.rotate(180);
            } else if (rotateRight) {
                ip.rotate(90);
            } else if (rotateLeft) {
                ip.rotate(-90);
            }
        }

        ip.advance();
        if (playfield.hasFallenOffEdge(ip)) {
            halted = true;
        }

        needsInput = false;
        return errors;
    }

    /*
     * I/O is the same as 2L:
     * "The two leftmost tape locations, called TL0 (Tape Location 0) and TL1 (Tape Location 1)
     * respectively, are significant. TL1 doesn't actually hold a value, it merely causes an I/O
     * operation if you attempt to increment or decrement it. If the value at TL0 is 0, and you
     * attempt to change the value of TL1, a character will be read from input into TL0. If TL0
     * is not 0, and you attempt to change the value of TL1, a character will be outputted from
     * the value of TL0."
     */
    private boolean doIO(World world) {
        ByteElement value = tape.read(0);
        if (value.isZero()) {
            CharacterElement c = world.inputCharacter();
            if (c == null) {
                needsInput = true;
                return false;
            }
            tape.write(0, new ByteElement(c.getChar()));
        } else {
            world.output(new CharacterElement(value.toChar()));
        }
        return true;
    }

    public Playfield getPlayfield(int index) {
        if (index == 0)
            return playfield;
        return null;
    }

    public Tape getTape(int index) {
        if (index == 0)
            return tape;
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
        return tapeView;
    }

    public String exportToText() {
        return playfield.dump();
    }

    public boolean hasHalted() {
        return halted;
    }

    public boolean needsInput() {
        return needsInput;
    }

    public void setOption(String name, boolean value) {
        if (name.equals("1L_AOI_EU")) {
            eu = value;
        } else {
            // error
        }
    }
}
