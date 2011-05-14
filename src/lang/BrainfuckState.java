// encoding: utf-8
/*
 * A BrainfuckState implements the semantics of brainfuck.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.bf;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class Brainfuck extends TextBasedLanguage<BrainfuckState> {
    public String getName() {
        return "brainfuck";
    }

    public List<String> exampleProgramNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("revcat");
        return names;
    }

    public BrainfuckState loadExampleProgram(int index) {
        String[][] program = {
          // Example program composed by Chris Pressey
          // (I got the idea from Rev. Null's talk at PyCon,
          // but this is a clean-room re-implementation,
          // which is probably exactly the same because the
          // problem is so simple, but that's a coincidence.)
          // This example program is in the public domain.
          {
            ">,[>,]<[.<]",
          }
        };
        return importFromText(program[index]);
    }

    public BrainfuckState importFromText(String text) {
        BrainfuckState s = new BrainfuckState();
        s.setProgramText(text);
        return s;
    }

    private static final String[][] properties = {
        {"Author", "Urban Müller"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "In this implementation, tape cells can contain values from " +
         "0 to 255 inclusive, and the tape is not arbitrarily " +
         "bounded in length.  A NUL character can be entered into " +
         "the input text box by pressing Ctrl+0.  Java2D has problems " +
         "highlighting regions of JTextAreas which are not focused, " +
         "so you may need to click into the main JTextArea which " +
         "displays the program in order to see the currently executing " +
         "position be highlighted therein while the program runs."},
    };

    public String[][] getProperties() {
        return properties;
    }
}

public class BrainfuckState implements State {
    protected BasicTape<ByteElement> tape;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected String program;
    protected int pc = 0;
    private static final Brainfuck language = new Brainfuck();

    public BrainfuckState() {
        tape = new BasicTape<ByteElement>(new ByteElement(0));
        tapeView = new BasicTapeView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public BrainfuckState clone() {
        BrainfuckState c = new BrainfuckState();
        c.tape = tape.clone();
        c.program = program;
        c.pc = pc;
        c.halted = halted;
        c.needsInput = needsInput;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicHead<ByteElement> h = tape.getHead(0);
        ByteElement b = h.read();
        char instruction = program.charAt(pc);

        switch (instruction) {
            case '<':
                // < LEFT  Move the memory pointer to the left
                h.move(-1);
                break;
            case '>':
                // > RIGHT Move the memory pointer to the right
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
                // , READ  Read a byte into current memory cell
                CharacterElement c = world.inputCharacter();
                if (c == null) {
                    needsInput = true;
                    return errors;
                }
                h.write(new ByteElement(c.getChar()));
                break;
            case '.':
                // . WRITE Write a byte from current memory cell
                world.output(new CharacterElement(b.toChar()));
                break;
            case '[':
                // [ WHILE Begin a while loop
                if (b.isZero()) {
                    // skip forwards to matching ]
                    int depth = 0;
                    for (;;) {
                        if (program.charAt(pc) == '[') {
                            depth++;
                        } else if (program.charAt(pc) == ']') {
                            depth--;
                            if (depth == 0)
                                break;
                        }
                        pc++;
                        if (pc >= program.length()) {
                            halted = true;
                            return errors;
                        }
                    }
                }
                break;
            case ']':
                // ] END End a while loop
                // skip backwards to matching ]
                int depth = 0;
                for (;;) {
                    if (program.charAt(pc) == '[') {
                        depth--;
                    } else if (program.charAt(pc) == ']') {
                        depth++;
                    }
                    pc--;
                    if (depth == 0 || pc < 0)
                        break;
                }
                break;
            default:
                // NOP
                break;
        }

        pc++;
        if (pc >= program.length()) {
            halted = true;
        }

        needsInput = false;
        return errors;
    }

    public Playfield getPlayfield(int index) {
        return null;
    }

    public Tape getTape(int index) {
        if (index == 0)
            return tape;
        return null;
    }

    public String getProgramText() {
        return program;
    }

    public int getProgramPosition() {
        return pc;
    }

    public List<Error> setProgramText(String text) {
        ArrayList<Error> errors = new ArrayList<Error>();
        program = text;
        return errors;
    }

    public View getPlayfieldView(int index) {
        return null;
    }

    public View getTapeView(int index) {
        if (index == 0)
            return tapeView;
        return null;
    }

    public String exportToText() {
        return program;
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
