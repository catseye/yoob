/*
 * A WunnelState implements the semantics of Wunnel.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.wunnel;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.awt.Graphics;
import java.awt.Color;


class Wunnel implements Language {
    public String getName() {
        return "Wunnel";
    }

    public int numPlayfields() {
        return 2;
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
        names.add("bitwise cat");
        return names;
    }

    public WunnelState loadExampleProgram(int index) {
        String[][] program = {
          // From http://www.esolangs.org/wiki/Wunnel
          // By Chris Pressey.  From the esowiki, thus in the public domain.
          {
            "          o   ooo  o",
            "",
            "",
            "o",
            "o",
            "o",
            "o         o",
            "o         o",
            "o         o",
            "o         o",
            "o",
            "o        o     o",
            "o         o",
            "o",
            "o        o",
            "o              o",
            "o        o     o",
            "o              o",
            "",
            "         o",
            "o oooooooo     o",
            "         o",
            "         o",
            "         o",
            "",
            "         o    oooo o",
          }
        };
        WunnelState s = new WunnelState();
        s.playfield.load(program[index]);
        return s;
    }

    public WunnelState importFromText(String text) {
        WunnelState s = new WunnelState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    public static final String positiveGenus = "0689@%&QROPADBqeopadb";
  
    private static final String[][] properties = {
        {"Author", "Chris Pressey"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "This implementation assumes the user is viewing the program in a font in which " +
         "all of, and only, the following characters have a character genus greater than " +
         "zero:\n  " + positiveGenus + "\n" +
         "It also presents the operation table as an immutable, secondary Yoob playfield " +
         "for better visualization."}
    };

    public String[][] getProperties() {
        return properties;
    }
}

class Operation implements Element {
    public static Operation ROT = new Operation("ROT");
    public static Operation NOP = new Operation("NOP");
    public static Operation SHU = new Operation("SHU");
    public static Operation RIG = new Operation("RIG");
    public static Operation LEF = new Operation("LEF");
    public static Operation INP = new Operation("INP");
    public static Operation OUT = new Operation("OUT");
    public static Operation NEG = new Operation("NEG");
    public static Operation PLU = new Operation("PLU");
    public static Operation BLA = new Operation("BLA");
    public static Operation END = new Operation("END");
    public static Operation[][] table = {
        {ROT, ROT, SHU, NEG, PLU, NOP},
        {LEF, SHU, RIG, BLA, NOP, BLA},
        {RIG, INP, LEF, NOP, PLU, NEG},
        {NOP, OUT, SHU, SHU, LEF, END},
        {SHU, END, NOP, RIG, SHU, END},
        {ROT, NOP, ROT, ROT, ROT, ROT},
    };

    private String s;
    public Operation(String s) { this.s = s; }
    public String getName() { return s; }
    public Operation fromChar(char c) { return this; }
    public boolean equals(Element q) {
        if (q instanceof Operation) { return this == q; }
        return false;
    }
}

class OperationTable implements Playfield<Operation> {
    private static IntegerElement FIVE = new IntegerElement(5);
    private WrapCursor<Operation> cursor;

    public OperationTable() {
        cursor = new WrapCursor<Operation>(this);
        cursor.setDelta(0, 1);
    }

    public void set(IntegerElement x, IntegerElement y, Operation e) {
        // immutable! nothing happens!
    }

    public Operation get(IntegerElement x, IntegerElement y) {
        int xi = x.intValue() % 6;
        int yi = y.intValue() % 6;
        return Operation.table[yi][xi];
    }

    public Operation getDefault() {
        return Operation.NOP; // why not, eh?
    }

    public OperationTable clone() {
        OperationTable o = new OperationTable();
        o.cursor = cursor.clone();
        // immutable, so no need to update the pf that the cursor sees
        return o;
    }

    public IntegerElement getMinX() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMaxX() {
        return FIVE;
    }

    public IntegerElement getMinY() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMaxY() {
        return FIVE;
    }

    public int numCursors() {
        return 1;
    }

    public BasicCursor<Operation> getCursor(int index) {
        if (index == 0) return cursor;
        return null;
    }
}

class OpTableView extends BasicPlayfieldView {
    public int getPreferredCellWidth() {
        return 3;
    }

    public boolean getSquareOff() {
        return false;
    }
}

public class WunnelState implements State {
    static private final IntegerElement MINUS_ONE = new IntegerElement(-1);
    protected BasicTape<IntegerElement> tape; // XXX for now
    protected CommonPlayfield playfield;
    protected OperationTable opTable;
    protected BasicPlayfieldView pfView;
    protected OpTableView opView;
    protected BasicTapeView tapeView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    private static final Wunnel language = new Wunnel();
  
    public WunnelState() {
        // The tape cells can actually only hold -1, 0, 1
        tape = new BasicTape<IntegerElement>(new IntegerElement(0));

        /*
         * The instruction pointer starts at the upper left corner of the source file moving down.
         */
        playfield = new CommonPlayfield();
        playfield.getCursor(0).setDelta(0, 1); // initially going down
        opTable = new OperationTable();
        pfView = new BasicPlayfieldView();
        opView = new OpTableView();
        tapeView = new BasicTapeView();
    }

    public Language getLanguage() {
	return language;
    }

    public WunnelState clone() {
        WunnelState c = new WunnelState();
        c.playfield = this.playfield.clone();
        c.opTable = this.opTable.clone();
        c.tape = this.tape.clone();
        c.halted = halted;
        c.needsInput = needsInput;
        return c;
    }

    public boolean genusMoreThanZero(char c) { // not all of them
        for (int i = 0; i < language.positiveGenus.length(); i++) {
            if (language.positiveGenus.charAt(i) == c)
                return true;
        }
        return false;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicCursor<Operation> opp = opTable.getCursor(0);
        BasicHead<IntegerElement> h = tape.getHead(0);
        char instruction = ip.get().getChar();
        Operation k = opp.get();

        if (genusMoreThanZero(instruction)) {
            if (k == Operation.END) {
                halted = true;
                return errors;
            } else if (k == Operation.NOP) {
            } else if (k == Operation.SHU) {
                if (ip.isHeaded(-1, 0)) {
                    ip.setY(ip.getY().add(h.read().negate()));
                } else if (ip.isHeaded(1, 0)) {
                    ip.setY(ip.getY().add(h.read()));
                } else if (ip.isHeaded(0, -1)) {
                    ip.setX(ip.getX().add(h.read()));
                } else if (ip.isHeaded(0, 1)) {
                    ip.setX(ip.getX().add(h.read().negate()));
                }
            } else if (k == Operation.ROT) {
                ip.rotate(-90);
                opp.rotate(-90);
            } else if (k == Operation.LEF) {
                h.move(-1);
            } else if (k == Operation.RIG) {
                h.move(1);
            } else if (k == Operation.NEG) {
                h.write(MINUS_ONE);
            } else if (k == Operation.BLA) {
                h.write(IntegerElement.ZERO);
            } else if (k == Operation.PLU) {
                h.write(IntegerElement.ONE);
            } else if (k == Operation.OUT) {
                IntegerElement i = h.read();
                if (i.isZero()) {
                    world.output(new CharacterElement('0'));
                } else {
                    world.output(new CharacterElement('1'));
                }
            } else if (k == Operation.INP) {
                CharacterElement c = world.inputCharacter();
                if (c == null) {
                    needsInput = true;
                    return errors;
                }
                if (c.getChar() == '1') {
                    h.write(IntegerElement.ONE);
                } else {
                    h.write(IntegerElement.ZERO);
                }
            }
        } else {
            opp.advance();
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
        if (index == 1)
            return opTable;
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
        if (index == 0)
            return pfView;
        if (index == 1)
            return opView;
        return null;
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

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    public void setOption(String name, boolean value) {
    }
}
