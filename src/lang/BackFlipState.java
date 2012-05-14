/*
 * A BackFlipState implements the semantics of BackFlip.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.backflip;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;

class BackFlip implements Language {
    public String getName() {
        return "BackFlip";
    }

    public int numPlayfields() {
        return 1;
    }

    public int numTapes() {
        return 0;
    }

    public boolean hasProgramText() {
        return false;
    }

    public boolean hasInput() {
        return false;
    }

    public boolean hasOutput() {
        return false;
    }

    public List<String> exampleProgramNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("counter w/fixed mirror");
        names.add("decimal numeral register");
        return names;
    }

    public BackFlipState loadExampleProgram(int index) {
        // All examples from: http://www.esolangs.org/wiki/BackFlip
        // and presumably by ais523.
        // From the esowiki, thus in the public domain.
        String[][] program = {
          {
            "\\----------------------",
            "              ",
            "    V V V V V V V    V",
            "\\   />/>/>/>/>/>/    \\<",
            "   >\\>\\>\\>\\>\\>\\>\\<",
            "<   ^ ^ ^ ^ ^ ^ ^ ",
            "                  ",
            "                  ",
            "                  ",
            "                  ",
            "                     ^",
          },
          {
            "\\----------------------------",
            "                          \\",
            "\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\/",
            "              ",
            "   >  >  >  >  >  >  >  > V",
            " >> >> >> >> >> >> >> >> > V",
            "",
            "                        0 >/<",
            "                        1 ^/<",
            "                        2 ^/<",
            "                        3 ^/<",
            "                        4 ^/<",
            "                        5 ^/<",
            "                        6 ^/<",
            "                        7 ^/<",
            "                        8 ^/<",
            "                           ^",
          },
        };
        BackFlipState s = new BackFlipState();
        s.playfield.load(program[index]);
        return s;
    }

    public BackFlipState importFromText(String text) {
        BackFlipState s = new BackFlipState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Alex Smith"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "In this implementation, the width of the playfield need not be determined " +
         "by the length of the first line; instead, the length of whichever line is " +
         "the longest determines the width."}
    };

    public String[][] getProperties() {
        return properties;
    }
}

public class BackFlipState implements State {
    protected CommonPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected boolean halted = false;
    private static final BackFlip language = new BackFlip();
  
    public BackFlipState() {
        /* The instruction pointer starts to the left of the top-left
         * corner of the program, going right.
         */
        playfield = new CommonPlayfield();
        pfView = new BasicPlayfieldView();
    }
    
    public BackFlipState clone() {
        BackFlipState c = new BackFlipState();
        c.playfield = this.playfield.clone();
        c.halted = halted;
        return c;
    }

    public BackFlip getLanguage() {
        return language;
    }

    private CharacterElement getReverseArrow(BasicCursor<CharacterElement> ip) {
        char a = ' ';
        if (ip.isHeaded(0, -1))
            a = 'V';
        else if (ip.isHeaded(0, 1))
            a = '^';
        else if (ip.isHeaded(-1, 0))
            a = '>';
        else if (ip.isHeaded(1, 0))
            a = '<';
        /* else error */
        return new CharacterElement(a);
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        char instruction = ip.get().getChar();

        switch (instruction) {
            /*
             * If it encounters a backtracking direction change, the IP
             * moves in the direction suggested by that change, which
             * changes to point to the direction the IP came from.
             */
            case '<':
                ip.set(getReverseArrow(ip));
                ip.setDelta(-1, 0);
                break;
            case '>':
                ip.set(getReverseArrow(ip));
                ip.setDelta(1, 0);
                break;
            case '^':
                ip.set(getReverseArrow(ip));
                ip.setDelta(0, -1);
                break;
            case 'V':
                ip.set(getReverseArrow(ip));
                ip.setDelta(0, 1);
                break;
            /*
             * If it encounters a flipping mirror, the IP bounces off the
             * mirror like a ray of light would, and then the mirror changes
             * to the other sort of flipping mirror.
             */
            case '/':
                ip.set(new CharacterElement('\\'));
                ip.setDelta(ip.getDeltaY().negate(), ip.getDeltaX().negate());
                break;
            case '\\':
                ip.set(new CharacterElement('/'));
                ip.setDelta(ip.getDeltaY(), ip.getDeltaX());
                break;
            default:
                // nop
                break;
        }

        ip.advance();
        /*
         * If the IP goes off the edge of the file, execution terminates.
         * (Infinite loops are impossible in BackFlip, so this always happens).
         */
        if (playfield.hasFallenOffEdge(ip)) {
            halted = true;
        }

        return errors;
    }

    public Playfield getPlayfield(int index) {
        if (index == 0)
            return playfield;
        return null;
    }

    public Tape getTape(int index) {
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
        return null;
    }

    public String exportToText() {
        return playfield.dump();
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
