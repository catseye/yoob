/*
 * A TwoIllState implements the semantics of 2-ill.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.twoill;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class TwoIll implements Language {
    public String getName() {
        return "2-ill";
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
        names.add("example one");
        names.add("cat");
        names.add("tape test");
        return names;
    }

    public TwoIllState loadExampleProgram(int index) {
        // All examples from:
        // http://www.esolangs.org/wiki/2-ill
        // Various authors, including zzo38 and Chris Pressey.
        // From the esowiki, thus in the public domain.
        String[][] program = {
          {
            "'------'",
            "'@  @ @'",
            "'   @@ '",
            "'@#$ @ '",
            "'   #  '",
            "'      '",
            "'  @ @ '",
            "'  @@@ '",
            "'@    @'",
            "'------'"
          },
          {
            "@@@",
            "@$@",
            " | ",
            " # ",
            " | ",
            "@?@",
            "@@@"
          },
          {
            "@       @",
            "",
            "   @#   @",
            "   @   #@",
            "    @@  ",
            "@$#  @  ",
            "    @#  @"
          },
        };
        TwoIllState s = new TwoIllState();
        s.playfield.load(program[index]);
        return s;
    }

    public TwoIllState importFromText(String text) {
        TwoIllState s = new TwoIllState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Aaron 'Zzo38' Black"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "Details of the language were discussed with Zzo38 on the talk page " +
         "of the esowiki article for the language, as implementation proceeded."},
    };

    public String[][] getProperties() {
        return properties;
    }
}

class TwoIllPlayfield extends CommonPlayfield {
    public TwoIllPlayfield clone() {
        TwoIllPlayfield c = new TwoIllPlayfield();
        c.copyBackingStoreFrom(this);
        c.ip = ip.clone();
        return c;
    }

    public void loadChar(int x, int y, char c) {
        switch (c) {
            // $ indicates start position of program, going east. If hit, it has no effect.
            case '$':
                ip.setX(x);
                ip.setY(y);
                ip.setDelta(1, 0);
                break;
        }
        super.loadChar(x, y, c);
    }
}

public class TwoIllState implements State {
    protected BasicTape<BitElement> tape;
    protected TwoIllPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    private static final TwoIll language = new TwoIll();

    public TwoIllState() {
        // The memory is a tape (similar to Brainfuck) infinite on both directions and starts all zero,
        // each cell can be only value 0 or 1, no other values are possible.
        tape = new BasicTape<BitElement>(BitElement.ZERO);
        playfield = new TwoIllPlayfield();
        pfView = new BasicPlayfieldView();
        tapeView = new BasicTapeView();
    }

    public Language getLanguage() {
        return language;
    }
    
    public TwoIllState clone() {
        TwoIllState c = new TwoIllState();
        c.playfield = (TwoIllPlayfield)this.playfield.clone();
        c.tape = this.tape.clone();
        c.halted = halted;
        c.needsInput = needsInput;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicHead<BitElement> h = tape.getHead(0);
        BitElement bit = h.read();
        char instruction = ip.get().getChar();

        if (instruction == '@') {
            // @ turns program pointer clockwise if value at pointer is 1, or counter-clockwise if is 0.
            ip.rotate(bit.getBoolean() ? 90 : -90);
        } else if (instruction == '#') {
            // # has different effects depending on direction of program pointer:
            if (ip.isHeaded(0, -1)) {
                // North = output bit at tape pointer, skip 2 program cells
                world.output(bit);
                ip.advance(2);
            } else if (ip.isHeaded(0, 1)) {
                // South = input bit, store at tape pointer, skip 2 program cells
                CharacterElement c = world.inputCharacter();
                if (c == null) {
                    needsInput = true;
                    return errors;
                }
                bit = bit.fromChar(c.getChar());
                h.write(bit);
                ip.advance(2);
            } else if (ip.isHeaded(1, 0)) {
                // East = toggle bit at tape pointer and then move tape pointer 1 space forwards, skip 2 program cells 
                h.write(bit.invert());
                h.move(1);
                ip.advance(2);
            } else if (ip.isHeaded(-1, 0)) {
                // West = move tape pointer 1 space backwards, skip 2 program cells
                h.move(-1);
                ip.advance(2);
            } else {
                // TODO: add error to errors
            }
        } else {
            // Anything else does nothing and can be used for comments. 
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
