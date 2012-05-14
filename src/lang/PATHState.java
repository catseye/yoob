/*
 * A PATHState implements the semantics of PATH.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.path;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;


class PATH implements Language {
    public String getName() {
        return "PATH";
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
        names.add("hello, world");
        return names;
    }

    public PATHState loadExampleProgram(int index) {
        String[][] program = {
          // http://www.esolangs.org/wiki/Hello_world_program_in_esoteric_languages#PATH
          // Author unknown.  From the esowiki, thus in the public domain.
          {
            "\\/\\   /\\          /\\  /\\ ",
            "+++   ++          ++  ++",
            "+++   ++  /++++\\  ++  ++  /++++\\ ",
            "++\\++\\++  +    +  ++  ++  +    +",
            "++   +++  +/+++/  ++  ++  +    +",
            "++   +++  ++      ++  ++  +    +",
            "\\/   \\/\\  /\\+++   /\\  /\\  /++.+/  ",
            "                          \\        \\ ",
            "/++++.+++++++++++++++++++++++++++  /  \\ ",
            "                          /\\       -  -#",
            "                          ++       .  -.",
            "+      /  \\++++\\  /++++\\  ++  /---\\-  -+",
            "+  /\\  +  +    +  +    +  +.  -   .-  -}",
            "+  +.  +  +    +  +       +-  -   +-  ",
            ".  +}  +  +    +  +       +-  -   +-  -.",
            "\\.+/\\++/  \\++++/  +       +\\  /-.+/-  --",
            "           \\      /    \\.{/   \\    /  \\/",
          }
        };
        PATHState s = new PATHState();
        s.playfield.load(program[index]);
        return s;
    }

    public PATHState importFromText(String text) {
        PATHState s = new PATHState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Francis Rogers"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "None yet"},
    };

    public String[][] getProperties() {
        return properties;
    }

}

class PATHPlayfield extends CommonPlayfield {
    public PATHPlayfield clone() {
        PATHPlayfield c = new PATHPlayfield();
        c.copyBackingStoreFrom(this);
        c.ip = ip.clone();
        c.ip.setPlayfield(c);
        return c;
    }

    public void loadChar(int x, int y, char c) {
        /*
         * A dollar sign ($), if present, indicates the initial position of the instruction pointer.
         * If none is present, the instruction pointer starts at the first character in the top left.
         * Either way, the initial direction is right.
         */
        switch (c) {
            case '$':
                ip.setX(x);
                ip.setY(y);
                ip.setDelta(1, 0);
                break;
        }
        super.loadChar(x, y, c);
    }
}

public class PATHState implements State {
    protected BasicTape<ByteElement> tape;
    protected PATHPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    private static final PATH language = new PATH();

    public PATHState() {
        tape = new BasicTape<ByteElement>(new ByteElement(0));
        playfield = new PATHPlayfield();
        pfView = new BasicPlayfieldView();
        tapeView = new BasicTapeView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public PATHState clone() {
        PATHState c = new PATHState();
        c.playfield = playfield.clone();
        c.tape = tape.clone();
        c.halted = halted;
        c.needsInput = needsInput;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicHead<ByteElement> h = tape.getHead(0);
        ByteElement b = h.read();
        char instruction = ip.get().getChar();

        switch (instruction) {
            case '{':
                // { LEFT  Move the memory pointer to the left
                h.move(-1);
                break;
            case '}':
                // } RIGHT Move the memory pointer to the right
                h.move(1);
                break;
            case '+':
                // + INCR  Increment current memory cell
                h.write(b.succ());
                break;
            case '-':
                // - DECR  Decrement current memory cell
                h.write(b.pred());
                break;
            case ',':
                // , - Input an ASCII character from standard input into the current memory cell.
                CharacterElement c = world.inputCharacter();
                if (c == null) {
                    needsInput = true;
                    return errors;
                }
                h.write(new ByteElement(c.getChar()));
                break;
            case '.':
                // . - Output an ASCII character from the current memory cell into standard output.
                world.output(new CharacterElement(b.toChar()));
                break;
            case '\\':
                // \ LURD  (Reflect as a ray of light would)
                ip.setDelta(ip.getDeltaY(), ip.getDeltaX());
                break;
            case '/':
                // / RULD  (Reflect as a ray of light would)
                ip.setDelta(ip.getDeltaY().negate(), ip.getDeltaX().negate());
                break;
            case '!':
                //             ! - Jump over the next symbol.
                ip.advance();
                break;
            case '<':
                //  < - If the value of the current memory cell is not 0, turn left.
                if (!b.isZero()) {
                    ip.setDelta(-1, 0);
                }
                break;
            case '>':
                //  > - If the value of the current memory cell is not 0, turn right.
                if (!b.isZero()) {
                    ip.setDelta(1, 0);
                }
                break;
            case '^':
                //  ^ - If the value of the current memory cell is not 0, turn up.
                if (!b.isZero()) {
                    ip.setDelta(0, -1);
                }
                break;
            case 'v':
                //  v - If the value of the current memory cell is not 0, turn down.
                if (!b.isZero()) {
                    ip.setDelta(0, 1);
                }
                break;
            case '#':
                //             # - End the program.
                halted = true;
                break;
            default:
                // NOP
                break;
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
