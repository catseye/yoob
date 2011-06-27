/*
 * A SceqlState implements the semantics of Sceql.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.sceql;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.net.URL;
import java.net.MalformedURLException;


class Sceql extends TextBasedLanguage<SceqlState> {
    private ArrayList<ExampleProgram> examples = null;

    public String getName() {
        return "Sceql";
    }

    private void loadExamples() {
        if (examples == null) {
            examples = new ArrayList<ExampleProgram>();
            try {
                examples.add(new ExampleProgram(
                    "99 Bottles of Beer (Keymaker)", new URL("http://yiap.nfshost.com/esoteric/sceql/beer.sceql")
                ));
                examples.add(new ExampleProgram(
                    "Quine (Keymaker)", new URL("http://yiap.nfshost.com/esoteric/sceql/quine.sceql")
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

    public SceqlState loadExampleProgram(int index) {
        loadExamples();
        return importFromText(examples.get(index).getText());
    }

    public SceqlState importFromText(String text) {
        SceqlState s = new SceqlState();
        s.setProgramText(text);
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
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

public class SceqlState implements State {
    protected BasicQueue<ByteElement> queue;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected String program;
    protected int pc = 0;
    private static final Sceql language = new Sceql();

    public SceqlState() {
        queue = new BasicQueue<ByteElement>(ByteElement.ZERO);
        tapeView = new BasicTapeView();
    }

    public Language getLanguage() {
        return language;
    }

    public SceqlState clone() {
        SceqlState c = new SceqlState();
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
                // - DEC    Decrement the byte that would be dequeued next (wrapping)
                b = queue.dequeue();
                queue.enqueueAtHead(b.pred());
                break;
            case '_':
                // _ INC    Increment the byte that would be dequeued next (wrapping)
                b = queue.dequeue();
                queue.enqueueAtHead(b.succ());
                break;
            case '\\':
                // \ BEGIN  Skip to the instruction after the corresponding END if the byte
                // that would be dequeued next is zero
                b = queue.peek();
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
            case '!':
                // ! GROW   Enqueue a new zero byte
                queue.enqueue(ByteElement.ZERO);
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
                // * OUTPUT Dequeue a byte, write it to stdout, and enqueue it again
                b = queue.dequeue();
                world.output(new CharacterElement(b.toChar()));
                queue.enqueue(b);
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
