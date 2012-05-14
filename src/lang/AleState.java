/*
 * An AleState implements the semantics of Ale.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.ale;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.net.URL;
import java.net.MalformedURLException;

class Ale extends TextBasedLanguage<AleState> {
    private ArrayList<ExampleProgram> examples = null;

    public String getName() {
        return "Ale";
    }

    public int numTapes() {
        return 2;
    }

    private void loadExamples() {
        if (examples == null) {
            examples = new ArrayList<ExampleProgram>();
            String[][] properties = {
                {"Author", "David Chipping"},
            };
            try {
                examples.add(new ExampleProgram(
                    "Hello, world", new URL("http://esoteric.voxelperfect.net/files/ale/src/hello.ale"), properties
                ));
                examples.add(new ExampleProgram(
                    "ASCII table backwards", new URL("http://esoteric.voxelperfect.net/files/ale/src/ascii_rev.ale"), properties
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

    public AleState loadExampleProgram(int index) {
        loadExamples();
        return importFromText(examples.get(index).getText());
    }

    public AleState importFromText(String text) {
        AleState s = new AleState();
        s.setProgramText(text);
        return s;
    }

    private static final String[][] properties = {
        {"Author", "David Chipping"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "This language posed some challenges to implementation in the yoob framework; namely, tape heads " +
         "being associated with more than one tape is not well supported.  Attempting to jump outside the " +
         "bounds of the program halts the program.  Writing to the data tape is allowed.  Attempting to " +
         "move outside the 8-cell bounds of the data tape results in wrapping to the other end."},
    };

    public String[][] getProperties() {
        return properties;
    }
}

/*
 * A SharedHead is a BasicHead that may be shared among Tapes.
 * The Tape it addresses at any given time is referred to by name.
 * This extra indirection eases keeping everything together after
 * cloning the program state.
 */
class SharedHead<E extends Element> extends BasicHead<E> {
    protected String tapeName;

    public SharedHead(String tapeName, IntegerElement pos) {
        super(null, pos);
        this.tapeName = tapeName;
    }

    public void setTapeName(String tapeName) {
        this.tapeName = tapeName;
    }

    public String getTapeName() {
        return tapeName;
    }

    public SharedHead<E> clone() {
        return new SharedHead<E>(tapeName, getPos());
    }
}

/*
 * A SharedHeadTape is a Tape which addressed by Heads which, unlike in say
 * BasicTape, are not "owned" by the Tape, but rather shared among tapes.
 * Every SharedHeadTape has access to a pool of Heads.  Each Head is
 * addressible by name.  I don't know where this is going.
 */
class SharedHeadTape<E extends Element> extends BasicTape<E> {
    protected List<SharedHead<E>> heads;

    public SharedHeadTape(E def) {
        super(def);
    }

    public void setHeads(List<SharedHead<E>> heads) {
        this.heads = heads;
    }

    // TODO: copyBackingStore, like in playfields
    public SharedHeadTape<E> clone() {
        SharedHeadTape<E> c = new SharedHeadTape<E>(def);
        c.store = new HashMap<IntegerElement, E>((Map<IntegerElement, E>)this.store);
        c.min = this.min;
        c.max = this.max;
        c.heads = this.heads;
        /* DOES NOT CLONE HEADS.  That's up to whatever is cloning this. */
        return c;
    }

    public int numHeads() {
        int n = 0;
        for (SharedHead h : heads) {
            if (h.getTape() == this) n++;
        }
        return n;
    }

    public SharedHead<E> getHead(int index) {
        for (SharedHead<E> h : heads) {
            if (h.getTape() == this) {
                if (index == 0)
                    return h;
                index--;
            }
        }
        return null;
    }
}

public class AleState implements State {
    protected SharedHeadTape<IntegerElement> dataTape, storageTape;
    protected List<SharedHead<IntegerElement>> heads;
    protected int current = 0;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected String program;
    protected int pc = 0;
    private static final Ale language = new Ale();
    private static final IntegerElement dataLength = new IntegerElement(8);
    private static final IntegerElement TWO = new IntegerElement(2);

    public AleState() {
        dataTape = new SharedHeadTape<IntegerElement>(IntegerElement.ZERO);
        IntegerElement i, j;
        for (i = IntegerElement.ZERO, j = IntegerElement.ONE;
             i.compareTo(dataLength) < 0;
             i = i.succ(), j = j.multiply(TWO)) {
            dataTape.write(i, j);
        }
        storageTape = new SharedHeadTape<IntegerElement>(IntegerElement.ZERO);
        heads = new ArrayList<SharedHead<IntegerElement>>(2);
        /* Both pointers will be initialised to the first cell of the storage tape. */
        heads.add(0, new SharedHead<IntegerElement>("storage", IntegerElement.ZERO));
        heads.add(1, new SharedHead<IntegerElement>("storage", IntegerElement.ZERO));
        assignTapes();
        dataTape.setHeads(heads);
        storageTape.setHeads(heads);
        tapeView = new BasicTapeView();
    }

    public AleState(SharedHeadTape<IntegerElement> dataTape, SharedHeadTape<IntegerElement> storageTape,
                    List<SharedHead<IntegerElement>> heads) {
        this.dataTape = dataTape;
        this.storageTape = storageTape;
        this.heads = heads;
    }

    public Language getLanguage() {
        return language;
    }

    private SharedHead<IntegerElement> getCurrentHead() {
        return heads.get(current);
    }

    private SharedHead<IntegerElement> getOtherHead() {
        return heads.get(1-current);
    }

    private SharedHeadTape<IntegerElement> getTapeByName(String name) {
        if (name.equals("data")) {
            return dataTape;
        } else {
            return storageTape;
        }
    }

    protected void assignTapes() {
        for (int i = 0; i <= 1; i++) {
            SharedHead<IntegerElement> head = heads.get(i);
            head.setTape(getTapeByName(head.getTapeName()));
        }
    }
    
    private void wrapHeadOnDataTape(SharedHead head) {
        while (head.getPos().compareTo(IntegerElement.ZERO) < 0) {
            head.setPos(head.getPos().add(dataLength));
        }
        while (head.getPos().compareTo(dataLength) >= 0) {
            head.setPos(head.getPos().subtract(dataLength));
        }
    }

    public AleState clone() {
        AleState c = new AleState(dataTape.clone(), storageTape.clone(), heads);
        c.current = current;
        // Clone the shared heads here:
        for (int i = 0; i <= 1; i++) {
            c.heads.set(i, heads.get(i).clone());
        }
        c.assignTapes();
        c.program = program;
        c.pc = pc;
        c.halted = halted;
        c.needsInput = needsInput;
        c.tapeView = tapeView;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        SharedHead<IntegerElement> currentHead = getCurrentHead();
        IntegerElement i = currentHead.read();
        IntegerElement j = getOtherHead().read();
        char instruction = program.charAt(pc);

        switch (instruction) {
            case '\\':
                // \ Make the other pointer become the current pointer (swap roles)
                current = 1-current;
                break;
            case '/':
                // / Make the current pointer address the other tape
                String n = currentHead.getTapeName();
                if (n.equals("data")) {
                    n = "storage";
                } else {
                    n = "data";
                }
                currentHead.setTapeName(n);
                assignTapes();
                currentHead.setPos(0);
                break;
            case '>':
                // > Move the current pointer forward by one tape cell
                currentHead.move(1);
                if (currentHead.getTapeName().equals("data")) {
                    wrapHeadOnDataTape(currentHead);
                }
                break;
            case '<':
                // < Move the current pointer backward by one tape cell
                currentHead.move(-1);
                if (currentHead.getTapeName().equals("data")) {
                    wrapHeadOnDataTape(currentHead);
                }
                break;
            case '+':
                // + Add the value under the other pointer to the value under the current pointer
                currentHead.write(i.add(j));
                break;
            case '-':
                // - Subtract the value under the other pointer from the value under the current pointer
                currentHead.write(i.subtract(j));
                break;
            case '!':
                // ! Output the value under the current pointer; if nothing, get input
                if (i.isZero()) {
                    CharacterElement c = world.inputCharacter();
                    if (c == null) {
                        needsInput = true;
                        return errors;
                    }
                    currentHead.write(new IntegerElement(c.getChar()));
                } else {
                    world.output(new CharacterElement(i.intValue()));
                }
                break;
            case ':':
                // : If the value under the current pointer is nonzero, increase the program counter by the value under the other pointer
                if (!i.isZero()) {
                    int newPC = pc + j.intValue();
                    if (newPC >= 0 && newPC < program.length()) {
                        pc = newPC;
                    } else {
                        halted = true;
                        return errors;
                    }
                }
                break;
            default:
                // NOP
                break;
        }

        //storageTape.dump();
        //dataTape.dump();

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
            return dataTape;
        if (index == 1)
            return storageTape;
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
        if (index >= 0 && index <= 1)
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
