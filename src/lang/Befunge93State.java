/*
 * A Befunge93State implements the semantics of Befunge-93.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.befunge93;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.awt.Graphics;
import java.awt.Color;


class Befunge93 implements Language {
    public String getName() {
        return "Befunge-93";
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
        names.add("hello, world mk ii");
        return names;
    }

    public Befunge93State loadExampleProgram(int index) {
        String[][] program = {
          {
            // Example program written by Chris Pressey.
            // Placed into the public domain.
            "                 v",
            ">v\"Hello world!\"0<",
            ",:",
            "^_25*,@",
          },
          {
            // Example program written by Chris Pressey.
            // Placed into the public domain.
            "v       <",
            ">0#v # \"Hello, World!\" # v#0  <",
            "  >v    #               >v",
            "  ,:                    ,:",
            "  ^_25*,^               ^_25*,^",
          },
        };
        Befunge93State s = new Befunge93State();
        s.playfield.load(program[index]);
        return s;
    }

    public Befunge93State importFromText(String text) {
        Befunge93State s = new Befunge93State();
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
         "Reading integers from input (&) does not have the same semantics " +
         "as the 'bef' interpreter, but those semantics (naively " +
         "relying on C's scanf) are pretty crappy anyway, and we figure that " +
         "any Befunge-93 program that needs to read integers reliably will " +
         "read them character by character anyway.  In this implementation, & " +
         "reads digits until the first non-digit, which it consumes.  If there " +
         "are no digits at all, it consumes a character and pushes zero."},
    };

    public String[][] getProperties() {
        return properties;
    }

}

class Befunge93Playfield extends BasicPlayfield<CharacterElement> {
    protected WrapCursor<CharacterElement> pc = null;

    public Befunge93Playfield() {
        super(new CharacterElement(' '));
        clear();
    }

    public void clear() {
        super.clear();
        pc = new WrapCursor<CharacterElement>(this, IntegerElement.ZERO, IntegerElement.ZERO, IntegerElement.ONE, IntegerElement.ZERO);
    }

    public Befunge93Playfield clone() {
        Befunge93Playfield c = new Befunge93Playfield();
        c.copyBackingStoreFrom(this);
        c.pc = pc.clone();
        c.pc.setPlayfield(c);
        return c;
    }

    public IntegerElement getMinX() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMaxX() {
        return new IntegerElement(79);
    }

    public IntegerElement getMinY() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMaxY() {
        return new IntegerElement(24);
    }

    public int numCursors() {
        return 1;
    }

    public BasicCursor<CharacterElement> getCursor(int index) {
        if (index == 0)
            return pc;
        return null;
    }

    public void loadChar(int x, int y, char c) {
        if (x < 0 || x > 79 || y < 0 || y > 24) {
            return;
        }
        set(x, y, new CharacterElement(c));
    }
    
    public String dumpElement(CharacterElement e) {
        return e.getName();
    }
}

class Befunge93PlayfieldView extends BasicPlayfieldView {
}

public class Befunge93State implements State {
    protected BasicStack<Int32Element> stack;
    protected Befunge93Playfield playfield;
    protected BasicTapeView stackView;
    protected Befunge93PlayfieldView pfView;
    protected boolean stringmode = false;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected Int32Element inputIntAcc = null;
    private static Random rand = new Random();
    private static final Befunge93 language = new Befunge93();

    public Befunge93State() {
        stack = new BasicStack<Int32Element>(new Int32Element(0));
        playfield = new Befunge93Playfield();
        pfView = new Befunge93PlayfieldView();
        stackView = new BasicTapeView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public Befunge93State clone() {
        Befunge93State c = new Befunge93State();
        c.playfield = playfield.clone();
        c.stack = stack.clone();
        c.stringmode = stringmode;
        c.halted = halted;
        c.needsInput = needsInput;
        c.inputIntAcc = inputIntAcc;
        return c;
    }

    /*
     * This has some limitations
     */
    private boolean readDigits(World world) {
        for (;;) {
            CharacterElement c = world.inputCharacter();
            if (c == null) {
                return false;
            } else if (c.isDigit()) {
                inputIntAcc = new Int32Element(inputIntAcc.getValue() * 10 + c.digitValue());
            } else {
                stack.push(inputIntAcc);
                inputIntAcc = null;
                return true;
            }
        }
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> pc = playfield.getCursor(0);
        CharacterElement i = pc.get();
        char instruction = i.getChar();
        Int32Element a, b;
        CharacterElement c;

        if (inputIntAcc != null) {
            boolean finished = readDigits(world);
            if (finished) {
                pc.advance();
                needsInput = false;
            } else {
                needsInput = true;
            }
            return errors;
        }

        if (stringmode) {
            if (instruction == '"') {
                stringmode = false;
            } else {
                stack.push(new Int32Element(instruction));
            }
        } else if (instruction >= '0' && instruction <= '9') {
            stack.push(new Int32Element((int)instruction - (int)'0'));
        } else switch (instruction) {
            case '+':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.add(b));
                break;
            case '-':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.subtract(b));
                break;
            case '*':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.multiply(b));
                break;
            case '/':
                b = stack.pop();
                a = stack.pop();
                if (b.isZero())
                    stack.push(Int32Element.ZERO);
                else
                    stack.push(a.divide(b));
                break;
            case '%':
                b = stack.pop();
                a = stack.pop();
                if (b.isZero())
                    stack.push(Int32Element.ZERO);
                else
                    stack.push(a.modulo(b));
                break;
            case '<':
                pc.setDelta(-1, 0);
                break;
            case '>':
                pc.setDelta(1, 0);
                break;
            case '^':
                pc.setDelta(0, -1);
                break;
            case 'v':
                pc.setDelta(0, 1);
                break;
            case '~':
                // ~ - Input an ASCII character from standard input and push onto stack.
                c = world.inputCharacter();
                if (c == null) {
                    needsInput = true;
                    return errors;
                }
                stack.push(new Int32Element(c.getChar()));
                break;
            case '&':
                // & - Input an integer (in ASCII characters, terminated by a non-digit)
                // from standard input and push onto stack.
                inputIntAcc = new Int32Element(0);
                boolean finished = readDigits(world);
                if (!finished) {
                    needsInput = true;
                    return errors;
                }
                break;
            case ',':
                // , - Pop a value off the stack and output as an ASCII character
                a = stack.pop();
                world.output(new CharacterElement(a.toChar()));
                break;
            case '.':
                // . - Pop a value off the stack and output as an decimal
                // integer followed by a space (all in ASCII)
                a = stack.pop();
                world.output(a);
                world.output(new CharacterElement(' '));
                break;
            case '#':
                // # - Jump over the next cell.
                pc.advance();
                break;
            case '@':
                // @ - End the program.
                halted = true;
                return errors;
            case '|':
                a = stack.pop();
                pc.setDelta(0, a.isZero() ? 1 : -1);
                break;
            case '_':
                a = stack.pop();
                pc.setDelta(a.isZero() ? 1 : -1, 0);
                break;
            case '$':
                a = stack.pop();
                break;
            case ':':
                a = stack.pop();
                stack.push(a);
                stack.push(a);
                break;
            case '\\':
                a = stack.pop();
                b = stack.pop();
                stack.push(a);
                stack.push(b);
                break;
            case '!':
                a = stack.pop();
                if (a.isZero())
                    stack.push(Int32Element.ONE);
                else
                    stack.push(Int32Element.ZERO);
                break;
            case '`':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.getValue() > b.getValue() ?
                           Int32Element.ONE : Int32Element.ZERO);
                break;
            case '"':
                stringmode = true;
                break;
            case '?':
                switch (rand.nextInt(4)) {
                    case 0:
                        pc.setDelta(0, -1); break;
                    case 1:
                        pc.setDelta(0, 1); break;
                    case 2:
                        pc.setDelta(-1, 0); break;
                    case 3:
                        pc.setDelta(1, 0); break;
                }
                break;
            case 'g':
                b = stack.pop();
                a = stack.pop();
                c = playfield.get(a.getValue(), b.getValue());
                stack.push(new Int32Element((int)c.getChar()));
                break;
            case 'p':
                b = stack.pop();
                a = stack.pop();
                Int32Element v = stack.pop();
                c = new CharacterElement(v.getValue());
                playfield.set(a.getValue(), b.getValue(), c);
                break;
            default:
                // NOP
                break;
        }

        pc.advance();
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
            return stack;
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
        return stackView;
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
