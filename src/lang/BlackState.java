/*
 * A BlackState implements the semantics of BackFlip.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.black;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;


class Black implements Language {
    public String getName() {
        return "Black";
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
        return true;
    }

    public List<String> exampleProgramNames() {
        ArrayList<String> names = new ArrayList<String>();
        names.add("one-splatter");
        return names;
    }

    public BlackState loadExampleProgram(int index) {
        // All examples from: http://www.esolangs.org/wiki/Black
        // and presumably by ais523.
        // From the esowiki, thus in the public domain.
        String[][] program = {
          {
            "                    1",
            "  #                # #          # ",
            "                                    N ",
            "  #                             #        ",
            "",
            "                            #",
            "",
            "         #                        #           #",
            "                               #",
            "   ",
            "    #",
            "      #     #",
            "",
            "                      #       #",
            "",
            "               #",
            "",
            "   #",
            "",
            "             #   #",
            "",
            "                 #        # #         #   ",
            "#     # #",
            "                                        ",
            "                #",
            "                                       #",
            "",
            "#",
            "                                      ",
            "                    %                  ",
            "      #                                  # ",
            "                  +              ",
            "     ##",
            "      #                 #            ",
            "                                           #",
            "                                        ",
            "        #                       ",
            "                 #               #",
            "                       +        ",
            "           ",
            "  #                              #",
            "",
            "                                    #  ",
            "     ##          #                  #",
            "              +                              ",
            "      #          #                           ",
            "        #",
            "                                      #",
            "",
            "                #",
            "   #",
            "",
            "                                            #",
            "",
            "                     ",
            "           *         *         ",
            "                   * ",
            "",
            "                    ",
            "                    !",
          },
        };
        BlackState s = new BlackState();
        s.playfield.load(program[index]);
        return s;
    }

    public BlackState importFromText(String text) {
        BlackState s = new BlackState();
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
         "This implementation includes the I/O extension by default; it is not " +
         "implemented as an option because it is central to demonstrating the " +
         "power of the example program."}
    };

    public String[][] getProperties() {
        return properties;
    }
}

public class BlackState implements State {
    protected CommonPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected boolean halted = false;
    private static final Black language = new Black();
  
    public BlackState() {
        /*
         * The instruction pointer starts at the third
         * row and third column of the input, going right. 
         */
        playfield = new CommonPlayfield();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        ip.setX(2);
        ip.setY(2);
        pfView = new BasicPlayfieldView();
    }

    public Language getLanguage() {
        return language;
    }
    
    public BlackState clone() {
        BlackState c = new BlackState();
        c.playfield = this.playfield.clone();
        c.halted = halted;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        char instruction = ip.get().getChar();

        switch (instruction) {
            case ' ':
              /*
               * If there is a non-space behind and to the left of the instruction
               * pointer (from the point of view of the direction the instruction
               * pointer is moving in), and the instruction pointer is not on a
               * non-space, the instruction pointer turns right.
               */
              int angle = 0;
              BasicCursor<CharacterElement> behindLeft = ip.clone();
              behindLeft.rotate(-135);
              behindLeft.advance();
              char lookBehindLeft = behindLeft.get().getChar();
              if (lookBehindLeft != ' ') angle += 90;

              /*
               * Likewise, if there is a non-space behind and to the right of the
               * instruction pointer, and the instruction pointer is not on a non-space,
               * the instruction pointer turns left.
               */
              BasicCursor<CharacterElement> behindRight = ip.clone();
              behindRight.rotate(135);
              behindRight.advance();
              char lookBehindRight = behindRight.get().getChar();
              if (lookBehindRight != ' ') angle -= 90;

              //System.out.printf("(%s,%s,%d)\n", lookBehindLeft, lookBehindRight, angle);

              ip.rotate(angle);
              /*
               * If both the two above conditions are true, the instruction pointer
               * continues in its original direction (thus, it is possible for the
               * instruction pointer to go between a pair of non-spaces).
               */
               // true because IP will have rotated 90 then -90
              break;
            default:
              /*
               * If the instruction pointer moves into a non-space, it pushes that
               * non-space one space in its own direction (leaving a space behind
               * in the cell it moved from), and causes the instruction pointer to
               * turn 180 degrees.
               * If the above action would push a non-space into a cell already
               * occupied by a non-space, program execution terminates.
               */
              BasicCursor<CharacterElement> ahead = ip.clone();
              ahead.advance();
              char lookahead = ahead.get().getChar();
              if (lookahead != ' ') {
                  halted = true;
                  return errors;
              }
              ip.set(new CharacterElement(' '));
              CharacterElement c = new CharacterElement(instruction);
              ahead.set(c);
              ip.rotate(180);
              if (instruction >= '0' && instruction <= '9') {
                  world.output(c);
              }
              if (instruction == 'N') {
                  world.output(new CharacterElement('\n'));
              }
              break;
        }

        ip.advance();

        // Not specified, but would result in an infinite loop from a literal reading
        // of the spec.  Detecting it and terminating is more convenient for us.
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
