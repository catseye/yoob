/*
 * A OneLaState implements the semantics of 1L_a.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.onela;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class OneLa implements Language {
    public String getName() {
        return "1L_a";
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
        names.add("output 65 in binary");
        return names;
    }

    public OneLaState loadExampleProgram(int index) {
        String[][] program = {
          {
            // Example program source derived from this image by Graue:
            // http://www.esolangs.org/wiki/Image:A.1l.png
            // which is on the esowiki, thus in the public domain.
            " ****************************************************** ***",
            " ****   *********************************************** ***",
            " **** *       ***************************************** ***",
            " ****    **** ***************************************** ***",
            " ******    ** ***************************************** ***",
            " *********  * ***************************************** ***",
            "            * ********  ******************************* ***",
            "************* **   ***   ****************************** ***",
            "***********   **         ****************************** ***",
            "*********** ****** **** ******************************* ***",
            "***********        ***  ******************************* ***",
            "********************** *        *********************** ***",
            "**********************   ****** *********************** ***",
            "******************************* *********************** ***",
            "******************************        ***************** ***",
            "******************************  ***** ***************** ***",
            "************************************  ***************** ***",
            "************************************ ***   ************ ***",
            "************************************     * ************ ***",
            "****************************************** ************ ***",
            "***************************************    ************ ***",
            "*************************************** *  *        *** ***",
            "***************************************      ****** *** ***",
            "*****************************************  ******   *** ***",
            "************************************************* ***** ***",
            "*************************************************       ***",
            "***********************************************************",
          }
        };
        OneLaState s = new OneLaState();
        s.playfield.load(program[index]);
        return s;
    }

    public OneLaState importFromText(String text) {
        OneLaState s = new OneLaState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Catatonic Porpoise"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "For the behavior when the IP travels off the right or bottom edge, I " +
         "selected terminating the program.  For the behavior of moving the tape " +
         "head left of its original position on the tape (which is also not defined " +
         "in the spec, though silently), I selected treating the tape as unbounded " +
         "in both directions."}
    };

    public String[][] getProperties() {
        return properties;
    }

}

public class OneLaState implements State {
    protected BasicTape<BitElement> tape;
    protected CommonPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    private static final OneLa language = new OneLa();

    public OneLaState() {
        /*
         * The memory is an array of bits (a tape), unbounded on the right. The leftmost cell is called TL0,
         * and the next couple cells are called TL1 and TL2. The data pointer starts out pointing to TL2.
         */
        // This tape is unbounded on the left, too; exceeding the left bound is undefined behaviour anyway
        tape = new BasicTape<BitElement>(BitElement.ZERO);
        BasicHead head = tape.getHead(0);
        head.setPos(new IntegerElement(2));

        /*
         * The instruction pointer starts at the upper left corner of the source file moving down, and the
         * program ends when the IP travels off the top or left side of the code. (Travelling off the right
         * or the bottom results in undefined behavior.) The IP moves one cell at a time in the current
         * direction, be that up, down, left, or right.
         */
        playfield = new CommonPlayfield();
        playfield.getCursor(0).setDelta(0, 1); // initially going down
        pfView = new BasicPlayfieldView();
        tapeView = new BasicTapeView();
    }

    public Language getLanguage() {
        return language;
    }
    
    public OneLaState clone() {
        OneLaState c = new OneLaState();
        c.playfield = this.playfield.clone();
        c.tape = this.tape.clone();
        c.halted = halted;
        c.needsInput = needsInput;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicHead<BitElement> h = tape.getHead(0);
        char instruction = ip.get().getChar();

        if (instruction == ' ') {
            if (ip.isHeaded(-1, 0)) {
                /*
                 * A space, if encountered by the IP moving left, moves the data pointer
                 * one cell to the left and then flips the bit at the data pointer.
                 */
                h.move(-1);
                BitElement bit = h.read();
                h.write(bit.invert());
                if (h.isAt(0)) {
                    /*
                     * As in 2L, I/O is memory-mapped. Flipping the bit at TL0 causes
                     * an input or output operation, as follows:
                     * If TL1 and TL2, then a 1 bit is output.
                     * If TL1 and not TL2, then a 0 bit is output.
                     * If not TL1, then a bit is input and saved in TL2.
                     */
                    boolean tl1 = tape.read(1).getBoolean();
                    boolean tl2 = tape.read(2).getBoolean();
                    if (tl1 && tl2) {
                        world.output(new CharacterElement('1'));
                    } else if (tl1 && !tl2) {
                        world.output(new CharacterElement('0'));
                    } else if (!tl1) {
                        CharacterElement c = world.inputCharacter();
                        if (c == null) {
                            needsInput = true;
                            h.write(bit.invert()); // UNFLIP
                            return errors;
                        }
                        tape.write(2, bit.fromChar(c.getChar()));
                    }
                }
            } else if (ip.isHeaded(0, -1)) {
                /*
                 * If a space is encountered by the IP moving up, the data pointer is
                 * moved one cell to the right.
                 */
                h.move(1);
            } else {
                /*
                 * A space encountered while moving down or right is a nop.
                 */
            }
        } else {
            /*
             * Everything else forms a "turning wall" like the + in 2L. The instruction
             * pointer moves backwards one space, then turns 90 degrees left if the
             * current bit is 0 or right if the current bit is 1.
             */
            ip.advance(-1);
            BitElement bit = h.read();
            if (bit.getBoolean()) {
               ip.rotate(90);
            } else {
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
    }
}
