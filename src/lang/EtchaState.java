/*
 * An EtchaState implements the semantics of Etcha.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.etcha;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;

import java.awt.Graphics;
import java.awt.Color;


class Etcha extends TextBasedLanguage<EtchaState> {
    public String getName() {
        return "Etcha";
    }

    public int numPlayfields() {
        return 1;
    }

    public int numTapes() {
        return 0;
    }

    public boolean hasInput() {
        return false;
    }

    public boolean hasOutput() {
        return false;
    }

    public List<String> exampleProgramNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("silly little thing");
        return names;
    }

    public EtchaState loadExampleProgram(int index) {
        String[][] program = {
          // Example from the Circute docs.
          // I hereby place it into the public domain.
          {
            ">+++>+++>+++>+++>[+]>>>>+",
          }
        };
        return importFromText(program[index]);
    }

    public EtchaState importFromText(String text) {
        EtchaState s = new EtchaState();
        s.setProgramText(text);
        return s;
    }

    private static final String[][] properties = {
        {"Author", "Chris Pressey"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "This implementation uses a yoob playfield as data " +
         "store/output."},
    };

    public String[][] getProperties() {
        return properties;
    }
}

class EtchaPlayfield extends BasicPlayfield<BitElement> {
    protected BasicCursor<BitElement> turtle;
    public EtchaPlayfield() {
        super(BitElement.ZERO);
        this.turtle = new BasicCursor<BitElement>(this);
        turtle.setDelta(0, -1);
        clear();
    }

    public EtchaPlayfield clone() {
        EtchaPlayfield c = new EtchaPlayfield();
        c.copyBackingStoreFrom(this);
        c.turtle = turtle.clone();
        c.turtle.setPlayfield(c);
        return c;
    }

    public int numCursors() {
        return 1;
    }

    public BasicCursor<BitElement> getCursor(int index) {
        if (index == 0)
            return turtle;
        return null;
    }
}

class EtchaPlayfieldView extends BasicPlayfieldView {
    public void render(Graphics g, Element e, int x, int y, int w, int h) {
        BitElement be = (BitElement)e;
        if (be.getBoolean()) {
            g.setColor(Color.black);
        } else {
            g.setColor(Color.white);
        }
        g.fillRect(x, y, w, h);
    }

    public void render(Graphics g, Cursor c, int x, int y, int w, int h) {
        g.setColor(Color.blue);
        g.drawRoundRect(x - 1, y - 1, w + 2, h + 2, w / 4, h / 4);
        if (c instanceof BasicCursor) {
            BasicCursor bc = (BasicCursor)c;
            int cx = x + (w/2);
            int cy = y + (h/2);
            int dx = bc.getDeltaX().intValue();
            int dy = bc.getDeltaY().intValue();
            int ex = cx + (dx * w);
            int ey = cy + (dy * h);
            g.drawLine(cx, cy, ex, ey);
        }
    }
}

public class EtchaState implements State {
    protected EtchaPlayfield pf;
    protected EtchaPlayfieldView pfView;
    protected int pencounter = 0;
    protected boolean pendown = true;
    protected boolean halted = false;
    protected String program;
    protected int pc = 0;
    private static final Etcha language = new Etcha();

    public EtchaState() {
        pf = new EtchaPlayfield();
        BasicCursor<BitElement> ip = (BasicCursor<BitElement>)pf.getCursor(0);
        ip.setDelta(0, -1);
        pfView = new EtchaPlayfieldView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public EtchaState clone() {
        EtchaState c = new EtchaState();
        c.pf = pf.clone();
        c.program = program;
        c.pc = pc;
        c.halted = halted;
        c.pencounter = pencounter;
        c.pendown = pendown;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<BitElement> ip = (BasicCursor<BitElement>)pf.getCursor(0);
        char instruction = program.charAt(pc);
        switch (instruction) {
            case '+':
                // + -- equivalent to FD 1
                if (pendown) {
                    ip.set(ip.get().invert());
                }
                ip.advance();
                break;
            case '>':
                // > -- equivalent to RT 90; toggles PU/PD every 4 executions
                ip.rotate(90);
                pencounter++;
                pencounter %= 4;
                if (pencounter == 0) {
                    pendown = !pendown;
                }
                break;
            case '[':
                // [ WHILE Begin a while loop
                if (ip.get().isZero()) {
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

        return errors;
    }

    public Playfield getPlayfield(int index) {
        if (index == 0)
            return pf;
        return null;
    }

    public Tape getTape(int index) {
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
        if (index == 0)
            return pfView;
        return null;
    }

    public View getTapeView(int index) {
        return null;
    }

    public String exportToText() {
        return program;
    }

    public boolean hasHalted() {
        return halted;
    }

    public boolean needsInput() {
        return false;
    }

    public void setOption(String name, boolean value) {
    }
}
