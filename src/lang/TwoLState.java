/*
 * A TwoLState (tries to) implement the semantics of 2L.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.twol;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;


class TwoL implements Language {
    public String getName() {
        return "2L";
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
        names.add("hello, world (mutable TL1)");
        return names;
    }

    public TwoLState loadExampleProgram(int index) {
        String[][] program = {
          {
            // From the esowiki, thus in the public domain.
            " *+ 2L \"Hello, World!\" program by poiuy_qwert   +",
            "+                                                   +",
            " *                                             +*",
            " *     +                                        *+",
            "    + **                                     +    *",
            "      +*                                           +",
            "     *  +                                                               *+",
            "     +                                        +                         *",
            "   +******************************************* *************************",
            "                                                 +                      +",
            "                                                     +",
            "                                                 *+",
            "    +                                          *+*",
            "      +                                        *+  *",
            "     *                                          +  *+",
            "     +                                           + *  +",
            "   +********                                       *      +",
            "                                                   + +*",
            "    +                                             *   *+",
            "      +                                           *+    *",
            "     *                                             +    *+",
            "     +                                              +   *    +",
            "   +******                                              **+",
            "                                                        +*",
            "                                                        +  *",
            "                                                           *+ +",
            "    +                                                *   +        +",
            "     ***+                                            *     * +*",
            "   +                                                       *  *+",
            "       *                                              +    +    *",
            "    +  +                                                  *   *+ +",
            "     ************************+                            * + * +",
            "   +                                                          *     +",
            "                            *                              +  + *+",
            "                            +                                  +*",
            "                                                              +   *",
            "    +                                                        *  *  +",
            "     *******************************************************+* +  *   +",
            "   +                                                              *       +",
            "                                                           *  +   +  +*",
            "    +                                                      +     *    *+",
            "      +                                                          *+     *",
            "     *                                                            + *    +",
            "     +                                                             + *  *",
            "   +************                                                        *    +",
            "     +                                                               *  +         +",
            "    *                                                                 +      * +",
            "    +                                                                  *+   +** ",
            "  +******************************************************************* *",
            "                                                                       +   +    *       +",
            "                                                                             *   +         +",
            "   +                                                                        +          +*",
            "    ***+                                                              *         *       *+",
            "  +                                                                             * ",
            "                                                                      *         +   +    *    +",
            "      *                                                                +              *   +        +",
            "   +  +                                                                              +        *+",
            "    *******+                                                                   *         *   +*",
            "  +                                                                                      *",
            "                                                                               *         +  +    *",
            "          *                                                                     +             *   +",
            "   +      +                                                                                  +",
            "    *****************************+                                                     *         *",
            "  +                                                                                              *",
            "                                                                                       *         +",
            "                                *                                                       +",
            "                                +",
            "  ************************************************************************+                    *",
            " +                                                                       *                     *",
            "                                                                                                +",
            "                                                                         +",
          },
        };
        TwoLState s = new TwoLState();
        s.playfield.load(program[index]);
        return s;
    }

    public TwoLState importFromText(String text) {
        TwoLState s = new TwoLState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("swap up and down for #");
        names.add("mutable TL1");
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Gregor Richards"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "This tries to implement 2L despite ambiguities in the spec, and lack of " +
         "working examples.  The meanings of up and down for the # instruction were " +
         "swapped in the original implementation, as this is available as an option " +
         "here.  Likewise, the only example program I have gotten to work requires " +
         "that TL1 is mutable despite a clear indication in the spec that it is not, " +
         "so that too is provided as an option.  For the behavior when the IP travels " +
         "off the right or bottom edge, I selected terminating the program.  Tape " +
         "cells may contain any integer from 0 to 255 inclusive; modifications are " +
         "made modulo 256."}
    };

    public String[][] getProperties() {
        return properties;
    }
}

public class TwoLState implements State {
    protected BasicTape<ByteElement> tape;
    protected CommonPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected boolean swapUpDown = false;
    protected boolean mutableTL1 = false;
    private static final TwoL language = new TwoL();
  
    public TwoLState() {
        tape = new BasicTape<ByteElement>(new ByteElement(0));
        BasicHead head = tape.getHead(0);
        // Tape head is initially on TL2.
        head.setPos(new IntegerElement(2));
        playfield = new CommonPlayfield();
        playfield.getCursor(0).setDelta(0, 1); // initially going down
        pfView = new BasicPlayfieldView();
        tapeView = new BasicTapeView();
    }
    
    public TwoLState clone() {
        TwoLState c = new TwoLState();
        c.playfield = this.playfield.clone();
        c.tape = this.tape.clone();
        c.halted = halted;
        c.needsInput = needsInput;
        c.swapUpDown = swapUpDown;
        c.mutableTL1 = mutableTL1;
        return c;
    }

    public Language getLanguage() {
        return language;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicHead<ByteElement> h = tape.getHead(0);
        ByteElement b = h.read();
        char instruction = ip.get().getChar();

        if (instruction == '*') {
            if (ip.isHeaded(0, -1)) {
                // Up: Move the data pointer to the right (> in Brainfuck) 
                if (swapUpDown)
                    h.move(-1);
                else
                    h.move(1);
            } else if (ip.isHeaded(0, 1)) {
                // Down: Move the data pointer to the left (< in Brainfuck) 
                if (swapUpDown)
                    h.move(1);
                else
                    h.move(-1);
            } else if (ip.isHeaded(1, 0)) {
                // Right: Increment the value at the data pointer (+ in Brainfuck) 
                if (h.isAt(1)) {
                    if (mutableTL1) {
                        h.write(b.succ());
                    }
                    if (!doIO(world)) return errors;
                } else {
                    h.write(b.succ());
                }
            } else if (ip.isHeaded(-1, 0)) {
                // Left: Decrement the value at the data pointer (- in Brainfuck) 
                if (h.isAt(1)) {
                    if (mutableTL1) {
                        h.write(b.pred());
                    }
                    if (!doIO(world)) return errors;
                } else {
                    h.write(b.pred());
                }
            } else {
                // TODO: add error to errors
            }
        }

        ip.advance();
        char lookahead = ip.get().getChar();
        ip.advance(-1);
        int turns = 0;
        while (lookahead == '+' && turns < 4) {
            b = h.read();
            if (!b.isZero()) {
               ip.rotate(90);
            } else {
               ip.rotate(-90);
            }
            turns++;
            ip.advance();
            lookahead = ip.get().getChar();
            ip.advance(-1);
        }
        // if turns >=4 we're surrounded! complain!

        ip.advance();
        if (playfield.hasFallenOffEdge(ip)) {
            halted = true;
        }

        needsInput = false;
        return errors;
    }

    /*
     * The two leftmost tape locations, called TL0 (Tape Location 0) and TL1 (Tape Location 1)
     * respectively, are significant. TL1 doesn't actually hold a value, it merely causes an I/O
     * operation if you attempt to increment or decrement it. If the value at TL0 is 0, and you
     * attempt to change the value of TL1, a character will be read from input into TL0. If TL0
     * is not 0, and you attempt to change the value of TL1, a character will be outputted from
     * the value of TL0.
     */
    private boolean doIO(World world) {
        ByteElement value = tape.read(IntegerElement.ZERO);
        if (value.isZero()) {
            CharacterElement c = world.inputCharacter();
            if (c == null) {
                needsInput = true;
                return false;
            }
            tape.write(IntegerElement.ZERO, new ByteElement(c.getChar()));
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
        if (name.equals("swap up and down for #")) {
            swapUpDown = value;
        } else if (name.equals("mutable TL1")) {
            mutableTL1 = value;
        } else {
            // error
        }
    }
}
