/*
 * A QdeqlState implements the semantics of Qdeql.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.qdeql;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.net.URL;
import java.net.MalformedURLException;


class Qdeql extends TextBasedLanguage<QdeqlState> {
    private ArrayList<ExampleProgram> examples = null;

    public String getName() {
        return "Qdeql";
    }

    private void loadExamples() {
        if (examples == null) {
            examples = new ArrayList<ExampleProgram>();
            try {
                examples.add(new ExampleProgram(
                    "hello, world", new URL("http://esoteric.voxelperfect.net/files/qdeql/src/hello.qd")
                ));
                examples.add(new ExampleProgram(
                    "cat", new URL("http://esoteric.voxelperfect.net/files/qdeql/src/cat.qd")
                ));
                examples.add(new ExampleProgram(
                    "loop_e", new URL("http://esoteric.voxelperfect.net/files/qdeql/src/loop_e.qd")
                ));
            } catch (MalformedURLException e) {
                // hmm.  That's too bad.
            }
        }
    }

    public List<String> exampleProgramNames() {
        loadExamples();
        ArrayList<String> names = new ArrayList<String>();
        for (ExampleProgram e : examples) {
            names.add(e.getName());
        }
        return names;
    }

    public QdeqlState loadExampleProgram(int index) {
        loadExamples();
        return importFromText(examples.get(index).getText());
    }

    public QdeqlState importFromText(String text) {
        QdeqlState s = new QdeqlState();
        s.setProgramText(text);
        return s;
    }

    private static final String[][] properties = {
        {"Author", "'Graue'"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "None yet."},
    };

    public String[][] getProperties() {
        return properties;
    }
}

public class QdeqlState implements State {
    protected BasicQueue<ByteElement> queue;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected String program;
    protected int pc = 0;
    private static final Qdeql language = new Qdeql();

    public QdeqlState() {
        queue = new BasicQueue<ByteElement>(ByteElement.ZERO);
        tapeView = new BasicTapeView();
    }

    public Language getLanguage() {
        return language;
    }

    public QdeqlState clone() {
        QdeqlState c = new QdeqlState();
        c.queue = queue.clone();
        c.program = program;
        c.pc = pc;
        c.halted = halted;
        c.needsInput = needsInput;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        ByteElement b;
        char instruction = program.charAt(pc);

        switch (instruction) {
            case '=':
                // = NEXT   Dequeue a byte and enqueue it again
                b = queue.dequeue();
                queue.enqueue(b);
                break;
            case '-':
                // - DEC    Dequeue a byte, subtract one (wrapping around), and enqueue it
                b = queue.dequeue();
                queue.enqueue(b.pred());
                break;
            case '\\':
                // \ BEGIN  Dequeue a byte and skip to the instruction after the
                // corresponding END if the byte is zero; otherwise, enqueue the
                // byte again, followed by two zero bytes
                b = queue.dequeue();
                if (b.isZero()) {
                    // skip forwards to matching /
                    int depth = 0;
                    for (;;) {
                        if (program.charAt(pc) == '\\') {
                            depth++;
                        } else if (program.charAt(pc) == '/') {
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
                } else {
                    queue.enqueue(b);
                    queue.enqueue(ByteElement.ZERO);
                    queue.enqueue(ByteElement.ZERO);
                }
                break;
            case '/':
                // / END    Go back to the corresponding BEGIN
                int depth = 0;
                for (;;) {
                    if (program.charAt(pc) == '\\') {
                        depth--;
                    } else if (program.charAt(pc) == '/') {
                        depth++;
                    }
                    pc--;
                    if (depth == 0 || pc < 0)
                        break;
                }
                break;
            case '&':
                // & INPUT  Read a byte from stdin and enqueue it (0 for EOF)
                CharacterElement c = world.inputCharacter();
                if (c == null) {
                    needsInput = true;
                    return errors;
                }
                queue.enqueue(new ByteElement(c.getChar()));
                break;
            case '*':
                // * OUTPUT Dequeue a byte and write it to stdout
                b = queue.dequeue();
                world.output(new CharacterElement(b.toChar()));
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
            return queue;
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
