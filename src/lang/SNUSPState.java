/*
 * A SNUSPState implements the semantics of Modular SNUSP.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.snusp;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;


class SNUSP implements Language {
    public String getName() {
        return "SNUSP";
    }

    public int numPlayfields() {
        return 1;
    }

    public int numTapes() {
        return 2;
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
        names.add("echo routine");
        names.add("multiplication");
        names.add("ethiopian multiplication");
        names.add("ackermann function");
        return names;
    }

    public SNUSPState loadExampleProgram(int index) {
        // All examples from:
        // http://www.esolangs.org/wiki/SNUSP
        // Authors unknown.  From the esowiki, thus in the public domain.
        String[][] program = {
          {
            "       /==!/======ECHO==,==.==#",
            "       |   |",
            "$==>==@/==@/==<==#",
          },
          {
            " read two characters    ,>,==\\  *    /=================== ATOI   ----------\\ ",
            " convert to integers /=/@</@=/  *   // /===== ITOA  ++++++++++\\ /----------/ ",
            "            multiply @ \\=!\\=========/ //           /++++++++++/ \\----------\\ ",
            "        convert back !/@!\\============/            \\++++++++++\\ /----------/ ",
            "and print the result \\/  \\.#    *                  /++++++++++/ \\--------#",
            "/====================/          *                  \\++++++++#",
            "|",
            "|    /-<+>\\                    #/?=<<<<\\!>>>>\\                   />>+<+<-\\ ",
            "|   #\\?===/! BMOV1 =====\\       \\->>>>+/    //  /======== BSPL2 !\\======?/#",
            "|    /->+<\\         /===|=========== FMOV4 =/  //                /<<+>+>-\\ ",
            "|   #\\?===/! FMOV1 =|===|==============\\  /====/  /====== FSPL2 !\\======?/#",
            "|                /==|===|==============|==|=======/",
            "|           * * *|* | * | * * * * * * *|* | * * *                /+<-\\ ",
            "|           * />@/<@/>>@/>>===\\ /====>>\\@<\\@<\\  *   /==== ADD2  !\\>=?/<#",
            "\\===== MUL2 =?/>@\\==<#<<<==\\  \\!\\<<<<@\\>>>>-?/\\ *  //            /-\\ ",
            "            *    \\\\        \\/@========|======</ * //  /== ZERO  !\\?/#",
            "            * * * \\\\* * * * | * * * * | * * * * *//  //",
            "                   \\\\       |         \\==========/  //",
            "                    \\======!\\=======================/",
          },
          {
            "    /==!/==atoi==@@@-@-----#",
            "    |   |          /-\\          /recurse\\    #/?\\ zero",
            "$>,@/>,@/?\\<=zero=!\\?/<=print==!\\@\\>?!\\@/<@\\.!\\-/",
            "        < @     #                 |   \\=/  \\=itoa=@@@+@+++++#",
            "     /==\\ \\===?!/===-?\\>>+# halve !     /+ !/+ !/+ !/+   \\    mod10",
            "#    !  @ |  #>>\\?-<+>/           /<+> -\\!?-\\!?-\\!?-\\!?-\\!",
            "/-<+>\\  > ?     />+<<++>-\\        \\?!\\-?!\\-?!\\-?!\\-?!\\-?/\\    div10",
            "?down?  | \\-<<<!\\=======?/\\ add &    #  +/! +/! +/! +/! +/",
            "\\>+<-/  | \\=<<<!/====?\\=\\ | double",
            "!    #  |       \\<++>-/ | |",
            "\\=======\\!@>============/!/",
          },
          {
            "   /==!/==atoi=@@@-@-----#",
            "   |   |",
            "   |   |       /=========\\!==\\!====\\   ** recursion **",
            "$,@/>,@/==ack=!\\?\\<+#    |   |     |   A(0,j) -> j+1",
            " j   i           \\<?\\+>-@/#  |     |   A(i,0) -> A(i-1,1)",
            "                    \\@\\>@\\->@/@\\<-@/#  A(i,j) -> A(i-1,A(i,j-1))",
            "            #      #  |  |     |",
            "            /-<<+>>\\!=/  \\=====|==@\\>>>@\\<<#  ",
            "  (a > 0)   ?      ?           |   |    |     ",
            "            \\>>+<<-/!==========/   |    |",
            "            #      #               |    |",
            "                                   |    |  ",
            "                    #/?========\\!==/    \\==!/=======?\\#",
            "                     \\->>+>+<<</            \\>>>+<<<-/",
          }
        };
        SNUSPState s = new SNUSPState();
        s.playfield.load(program[index]);
        return s;
    }

    public SNUSPState importFromText(String text) {
        SNUSPState s = new SNUSPState();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Daniel Brockman"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "The implementation implements Modular SNUSP by default; it was not " +
         "made an option, because almost all interesting examples require " +
         "Modular SNUSP."},
    };

    public String[][] getProperties() {
        return properties;
    }

}

class BasicCursorElement<E extends Element> implements Element {
    final protected Playfield<E> p;
    final protected IntegerElement x;
    final protected IntegerElement y;
    final protected IntegerElement dx;
    final protected IntegerElement dy;

    public BasicCursorElement(BasicCursor<E> c) {
        this.p = c.getPlayfield();
        this.x = c.getX();
        this.y = c.getY();
        this.dx = c.getDeltaX();
        this.dy = c.getDeltaY();
    }

    public String getName() {
        return "(" + dx + "," + dy + ")@(" + x + "," + y + ")";
    }

    public boolean equals(Element e) {
        if (e instanceof BasicCursorElement) {
            BasicCursorElement f = (BasicCursorElement)e;
            return p.equals(f.p) &&
                   x.equals(f.x) && y.equals(f.y) &&
                   dx.equals(f.dx) && dy.equals(f.dy);
        }
        return false;
    }

    // this makes no sense.  Codecs plz kthx
    public BasicCursorElement<E> fromChar(char c) {
        return null;
    }

    public BasicCursor<E> toBasicCursor() {
        return new BasicCursor<E>(p, x, y, dx, dy);
    }

}

class SNUSPPlayfield extends CommonPlayfield {
    public SNUSPPlayfield clone() {
        SNUSPPlayfield c = new SNUSPPlayfield();
        c.copyBackingStoreFrom(this);
        c.ip = ip.clone();
        return c;
    }

    public void loadChar(int x, int y, char c) {
        /*
         * A dollar sign ($), if present, indicates the initial position of the instruction pointer.
         * If none is present, the instruction pointer starts at the first character in the top left.
         * Either way, the initial direction is right.
         */
        switch (c) {
            case '$':
                ip.setX(x);
                ip.setY(y);
                ip.setDelta(1, 0);
                break;
        }
        super.loadChar(x, y, c);
    }
}

public class SNUSPState implements State {
    protected BasicTape<ByteElement> tape;
    protected BasicStack<BasicCursorElement<CharacterElement>> callStack;
    protected SNUSPPlayfield playfield;
    protected BasicPlayfieldView pfView;
    protected BasicTapeView tapeView;
    protected BasicTapeView stackView;
    protected boolean halted = false;
    protected boolean needsInput = false;
    private static final SNUSP language = new SNUSP();

    public SNUSPState() {
        playfield = new SNUSPPlayfield();
        tape = new BasicTape<ByteElement>(new ByteElement(0));
        callStack = new BasicStack<BasicCursorElement<CharacterElement>>(new BasicCursorElement<CharacterElement>(new BasicCursor<CharacterElement>(playfield)));
        pfView = new BasicPlayfieldView();
        tapeView = new BasicTapeView();
        stackView = new BasicTapeView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public SNUSPState clone() {
        SNUSPState c = new SNUSPState();
        c.playfield = playfield.clone();
        c.tape = tape.clone();
        c.halted = halted;
        c.needsInput = needsInput;
        c.callStack = callStack.clone();
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> ip = playfield.getCursor(0);
        BasicHead<ByteElement> h = tape.getHead(0);
        ByteElement b = h.read();
        char instruction = ip.get().getChar();

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
            case '\\':
                // \ LURD  (Reflect as a ray of light would)
                ip.setDelta(ip.getDeltaY(), ip.getDeltaX());
                break;
            case '/':
                // / RULD  (Reflect as a ray of light would)
                ip.setDelta(ip.getDeltaY().negate(), ip.getDeltaX().negate());
                break;
            case '!':
                // ! SKIP  Move the instruction pointer forward one step
                ip.advance();
                break;
            case '?':
                // ? SKIPZ If the current memory cell is zero, do a SKIP
                if (b.isZero()) {
                    ip.advance();
                }
                break;
            case '@':
                // @ ENTER Push the current direction and IP location on the call-stack
                callStack.push(new BasicCursorElement<CharacterElement>(ip));
                break;
            case '#':
                // # LEAVE Pop direction and IP location off call-stack and advance IP one step
                if (callStack.isEmpty()) {
                    halted = true;
                    return errors;
                }
                BasicCursorElement<CharacterElement> bce = callStack.pop();
                BasicCursor<CharacterElement> oldIp = bce.toBasicCursor();
                ip.setX(oldIp.getX());
                ip.setY(oldIp.getY());
                ip.setDelta(oldIp.getDeltaX(), oldIp.getDeltaY());
                ip.advance();
                break;
            default:
                // NOP
                break;
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
        return null;
    }

    public Tape getTape(int index) {
        if (index == 0)
            return tape;
        if (index == 1)
            return callStack;
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
        if (index == 0)
            return tapeView;
        if (index == 1)
            return stackView;
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
