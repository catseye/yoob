/*
 * A LNUSPState implements the semantics of LNUSP.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.lnusp;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.awt.Graphics;
import java.awt.Color;
import java.awt.Component;


class LNUSP implements Language {
    public String getName() {
        return "LNUSP";
    }

    public int numPlayfields() {
        return 2;
    }

    public int numTapes() {
        return 0;
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
        names.add("cat");
        names.add("demo of * and +");
        return names;
    }

    public LNUSPState loadExampleProgram(int index) {
        String[][] program = {
          // From http://www.esolangs.org/wiki/LNUSP
          // Author unknown, probably zzo38.  From the esowiki, thus in the public domain.
          {
              ".      .               .                .",
              " .?......!!................?            .",
              " ?.    .!  !           .    ?           .",
              " ? .   .!  !           .    ?           .",
              "  ?.!..@..!............@...?            .",
              "          !.............................@",
          },
          // Example made up by Chris Pressey to test this implementation
          // In the public domain with the rest of this file
          {
              ".",
              " .       !***?",
              "  .     !     ?",
              "   .          ?",
              "    !..***+++?",
              "        +",
          },
        };
        LNUSPState s = new LNUSPState();
        s.playfield.load(program[index]);
        return s;
    }

    public LNUSPState importFromText(String text) {
        LNUSPState s = new LNUSPState();
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
         "This implementation does not support the 'repeat' prefix on each line. " +
	 "It also treats all characters which are not one of the five defined " +
	 "instructions as nops. It also treats exceeding the bounds to the west, " +
	 "east, or south, as halting the program; exceeding the north bound when " +
         "not in one of the defined subtroutine columns is treated as a reflection." },
    };

    public String[][] getProperties() {
        return properties;
    }
}

class LNUSPDataSpace extends BasicPlayfield<ByteElement> {
    protected BasicCursor<ByteElement> dp = null;

    public LNUSPDataSpace() {
        super(new ByteElement(0));
        clear();
    }

    public void clear() {
        super.clear();
        dp = new BasicCursor<ByteElement>(this, IntegerElement.ZERO, IntegerElement.ZERO, IntegerElement.ZERO, IntegerElement.ZERO);
    }

    public LNUSPDataSpace clone() {
        LNUSPDataSpace c = new LNUSPDataSpace();
        c.copyBackingStoreFrom(this);
        c.dp = dp.clone();
        c.dp.setPlayfield(c);
        return c;
    }

    public int numCursors() {
        return 1;
    }

    public BasicCursor<ByteElement> getCursor(int index) {
        if (index == 0)
            return dp;
        return null;
    }

    public void loadChar(int x, int y, char c) {
        set(x, y, new ByteElement(c));
    }
}

class DataSpaceView extends BasicPlayfieldView {
    public int getPreferredCellWidth() {
        return 3;
    }

    public boolean getSquareOff() {
        return false;
    }

    public float getAlignmentX() {
        return Component.RIGHT_ALIGNMENT;
    }
}

public class LNUSPState implements State {
    protected LNUSPDataSpace dataspace;
    protected CommonPlayfield playfield;
    protected BasicPlayfieldView pfView, dsView;
    protected BasicCursor<CharacterElement> savedPosition;
    protected boolean halted = false;
    protected boolean needsInput = false;
    private static final LNUSP language = new LNUSP();

    public LNUSPState() {
        dataspace = new LNUSPDataSpace();
        playfield = new CommonPlayfield();
	/* The program starts in the top left corner going southeast. */
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
	ip.setDelta(1, 1);
	savedPosition = null;
        pfView = new BasicPlayfieldView();
	dsView = new DataSpaceView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public LNUSPState clone() {
        LNUSPState c = new LNUSPState();
        c.playfield = playfield.clone();
        c.dataspace = dataspace.clone();
	c.savedPosition = savedPosition;
	if (c.savedPosition != null) {
	    c.savedPosition.setPlayfield(c.playfield);
	}
        c.halted = halted;
        c.needsInput = needsInput;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicCursor<ByteElement> dp = dataspace.getCursor(0);
        ByteElement b = dp.get();
        char instruction = ip.get().getChar();

	/*
	+ Increase current memory cell by 1, mod 256
	* Move memory pointer in the direction the IP is traveling
	? Turn 45 degrees left if current memory cell is nonzero
	! Turn 45 degrees left if current memory cell is zero
	@ Save position and go north, or if there is already a
	  position saved, go back to that position and delete the
	  saved position, or if already going north, unconditionally
	  turn 45 degrees right
	. (dot) is specially guaranteed to not do anything.
	*/

        if (playfield.hasFallenOffEdge(ip)) {
	    if (ip.getY().compareTo(IntegerElement.ZERO) < 0) {
		/*
		 * Going off the north side of the program results in a
		 * subroutine being executed, followed by the instruction
		 * pointer reversing direction. Which subroutine is executed
		 * depends on how far over the column is from the left. With
		 * the leftmost column counting as 1, the columns are
		    * 8 = Input
		    * 24 = Output
		    * 41 = Stop 
		*/
		if (ip.getX().intValue() == 7) {
                    CharacterElement c = world.inputCharacter();
                    if (c == null) {
                        needsInput = true;
                        return errors;
                    }
                    dp.set(new ByteElement(c.getChar()));
		} else if (ip.getX().intValue() == 23) {
                    world.output(new CharacterElement(b.toChar()));
		} else if (ip.getX().intValue() == 40) {
		    halted = true;
                    return errors;
		}
                ip.setDeltaY(ip.getDeltaY().negate());
	    } else {
                halted = true;
                return errors;
	    }
        }

        switch (instruction) {
            case '?':
	        if (!b.isZero()) {
		    ip.rotate(-45);
	        }
                break;
            case '!':
	        if (b.isZero()) {
		    ip.rotate(-45);
	        }
                break;
            case '+':
                dp.set(b.succ());
                break;
            case '*':
                dp.move(ip.getDeltaX(), ip.getDeltaY());
                break;
            case '@':
		if (ip.isHeaded(0, -1)) {
		    ip.rotate(45);
	        } else if (savedPosition != null) {
		    ip.setX(savedPosition.getX());
                    ip.setY(savedPosition.getY());
		    ip.setDeltaX(savedPosition.getDeltaX());
                    ip.setDeltaY(savedPosition.getDeltaY());
		    savedPosition = null;
		} else {
		    savedPosition = ip.clone();
		    ip.setDelta(0, -1);
		}
                break;
            default:
                // NOP
                break;
        }

        ip.advance();
        needsInput = false;
        return errors;
    }

    public Playfield getPlayfield(int index) {
        if (index == 0)
            return playfield;
        if (index == 1)
            return dataspace;
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
        if (index == 0)
            return pfView;
        if (index == 1)
	    return dsView;
	return null;
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
        return needsInput;
    }

    public void setOption(String name, boolean value) {
    }
}
