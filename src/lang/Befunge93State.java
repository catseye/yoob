/*
 * A Befunge93State implements the semantics of Befunge-93.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.befunge93;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

import java.net.URL;
import java.net.MalformedURLException;

import java.awt.Graphics;
import java.awt.Color;


class Befunge93 implements Language {
    private ArrayList<ExampleProgram> examples = null;
  
    public String getName() {
        return "Befunge-93";
    }

    public int numPlayfields() {
        return 1;
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

    private void loadExamples() {
        if (examples != null) return;

        examples = new ArrayList<ExampleProgram>();
        String[][] cpProperties = {
            {"Author", "Chris Pressey"},
            {"License", "Public Domain"},
        };

        examples.add(new ExampleProgram(
            "cascade.bf",
            ">011p013p>11g13gg:84*`#v_84*>11g13g4+p$v\n" +
            "#     13p^p11         <>    ^        >  \n" +
            "          v                            <\n" +
            "#     13pv>11g1+:85*-#^_011p13g1+:4%#^_",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "drx.bf",
            "#v       #<                                      v\n" +
            ">v\"Hello... I'm Dr. X.  How do you feel today? \"0<\n" +
            ",:        #\n" +
            "^_$v     <\n" +
            "   >~25*-|                                          > v\n" +
            "         >                                       0#v?v\n" +
            "          ^\"Do your friends find this reasonable? \"<\n" +
            "          ^\"How long have you felt this way? \"      <\n" +
            "          ^\"How do you feel about that? \"            <\n" +
            "          ^\"Are you disturbed by this? \"              <",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "ea.bf",
            "100p            v\n" +
            " v\"love\"0     <\n" +
            " v\"power\"0   <\n" +
            " v\"strength\"0?^#<            <\n" +
            " v\"success\"0 ?v\n" +
            " v\"agony\"0   <\n" +
            ">v\"beauty\"0   <>025*\".\" 1v v_^\n" +
            ",:      >00g2- |        v< #:\n" +
            "^_,00g1-|      >0\" fo \"3>00p^<\n" +
            "        >0\" eht si \"2   ^  >,^",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "easm2.bf",
            ">801p          11v\n" +
            " v\"love\"      < >+v\n" +
            "#v\"power\"    <  1$:\n" +
            " v\"strength\" ?^# <0\n" +
            " v\"success\"  ?v g 1\n" +
            " v\"agony\"    <  1 p\n" +
            " v\"beauty\"    < 0 9\n" +
            "            v ,<p 1\n" +
            " >\" eht si \">: |g +\n" +
            " >\" fo \"> v    <1 -\n" +
            " >25*\".\"^ >\" \"10^^_v\n" +
            "^        p91p81:\">\"<\n" +
            "\n",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "fact.bf",
            "                                    v\n" +
            ">v\"Please enter a number (1-16) : \"0<\n" +
            ",:             >$*99g1-:99p#v_.25*,@\n" +
            "^_&:1-99p>:1-:!|10          < \n" +
            "         ^     <",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "hello.bf",
            "                 v\n" +
            ">v\"Hello world!\"0<\n" +
            ",:\n" +
            "^_25*,@",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "hex.bf",
            "~:25*-#v_@      >  >,\" \",\n" +
            "v      < >25*-\"A\"+v^+\"A\"-*52<\n" +
            ">:82*/:9`|      \" >,:82*%:9`|        \n" +
            "         >\"0\"+ #^ ^#    +\"0\"<",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "hwii.bf",
            "v       <\n" +
            ">0#v # \"Hello, World!\" # v#0  <\n" +
            "  >v    #               >v\n" +
            "  ,:                    ,:\n" +
            "  ^_25*,^               ^_25*,^",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "maze.bf",
            " v    <\n" +
            ">?\"/\",^\n" +
            " >\"\\\",^",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "numer.bf",
            "000p>~:25*-!#v_\"a\"-1+00g+00p> 00g9`#v_v\n" +
            "        @.g00<  vp00+%*52g00 /*52g00<\n" +
            "    ^           >#          ^#        <",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "pangram.bf",
            "v             <    >\"a\"-v\n" +
            "             >:\"`\"`|    >\"<\"\\5pv\n" +
            "       >:\"@\"`|#    >\"A\"-^\n" +
            ">~:91+-|     >^                <\n" +
            "       >                  v\n" +
            "vvvvvvvvvvvvvvvvvvvvvvvvvv< v\"yes\"\n" +
            "v<<<<<<<<<<<<<<<<<<<<<<<<<  ,\n" +
            ">\"on\"                       >,,91+,@\n",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "pascserp.bf",
            "58*00p010p>58*00g-|>0g#<1-10gg00g10v\n" +
            "v98p00:+1g00< v67<>    >1    v+g-1g<\n" +
            ">*7+-! #v_v>^^<  |%2pg0 1g00:<\n" +
            "v p00*58<  ^,<^48<>10g!|@\n" +
            ">52*,10g1+ :1 0p83 *- ! |\n" +
            "          v             <",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "rand.bf",
            "vv  <      <\n" +
            "    2      \n" +
            "    ^  v<\n" +
            " v1<?>3v4\n" +
            "    ^   ^\n" +
            ">  >?>  ?>5^\n" +
            "    v   v\n" +
            " v9<?>7v6\n" +
            "    v  v<\n" +
            "    8\n" +
            " .  >  >   ^\n" +
            "^<",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "rand3.bf",
            "7   $^>91+v\n" +
            "?95+vv?94+vv\n" +
            ">96+v9>93+v\n" +
            "# +  >#  v<#\n" +
            " >>>>  >>>.@\n" +
            " 123 >^456#9\n" +
            " ^?^#?#^?^ 7\n" +
            "^ ## <  8  +\n" +
            "> > ^#  < ^<",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "rand6.bf",
            "?<>>?<8>\n" +
            ">>?<>>?<\n" +
            "  2@4.+<\n" +
            "1+ + +<^",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "robot.bf",
            "vv_v#:\"*********\"*25<           01 = x coord\n" +
            "8,:  >              ^           02 = y coord\n" +
            "0>^  ^\"*     * *\"*25<\n" +
            "1    >              ^\n" +
            "p    ^\"* *** * *\"*25<\n" +
            "2    >              ^\n" +
            "0    ^\"* *     *\"*25<\n" +
            "2    >              ^\n" +
            "p    ^\"* * *   *\"*25<\n" +
            "\"    >              ^       \n" +
            "O    ^\"* ***** *\"*25<       >,v\n" +
            "\"    >              ^       |:<\"You hit a wall! Game over!\"0<\n" +
            "0    ^\"*     * *\"*25<       >25*,@                          |-*84gg20g10<\n" +
            "1    >              ^v ,*62                       pg20g10\"O\"<  <       \n" +
            "g    ^\"*   *   *\"*25<                                  >00g\"w\"-|\n" +
            "0    >              ^                          >00g\"e\"-|       >01g1-01p^\n" +
            "2    ^\"*********\"*250<                 >00g\"s\"-|       >01g1+01p        ^\n" +
            "g  > \" \"01g02gp         \"?\",~~$:00p\"n\"-|       >02g2+02p                ^\n" +
            ">p              62*, ^                 >02g2-02p                        ^",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "selflis2.bf",
            "\">:#,_66*2-,@This prints itself out backwards......  but it has to be 80x1 cells",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "testbrdg.bf",
            ">>>>v\n" +
            "@0.v>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>#\n" +
            "#<<<                                                                        @.1@\n",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "testmodu.bf",
            "v The original implementation of Befunge-93 was in ANSI C (a.k.a C89).\n" +
            "v The description of Befunge-93 did not describe how modulo should be\n" +
            "v implemented for a negative modulus -- it relied on ANSI C's semantics.\n" +
            "v\n" +
            "v Unfortunately, ANSI C did not define negative modulus either.\n" +
            "v\n" +
            "v So this program tests what your Befunge-93 implementation does for\n" +
            "v modulo by negative numbers.  If it outputs:\n" +
            "v\n" +
            "v  1 -1 : result has same sign as the dividend (like C99)\n" +
            "v -2  2 : result has same sign as the divisor  (like Python)\n" +
            "v\n" +
            "v Of course, since it is undefined, other results are possible.\n" +
            "v\n" +
            ">703-%.07-3%.@\n",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "testpfcl.bf",
            "000p>00g1+01p01g00g`#v_00g.000p>00g1-01p00g01g`#v_00g.@\n" +
            "    ^p00+1g00        <         ^p00-1g00        <\n",
            cpProperties
        ));
        examples.add(new ExampleProgram(
            "toupper.bf",
            "v,         <         <       <\n" +
            ">~:\"a\"1-`!#^_:\"z\"1+`#^_\"aA\"--^",
            cpProperties
        ));

    }
      
    public List<String> exampleProgramNames() {
        loadExamples();
        ArrayList<String> names = new ArrayList<String>();
        for (ExampleProgram e : examples) {
            names.add(e.getName());
        }
        return names;
    }

    public Befunge93State loadExampleProgram(int index) {
        loadExamples();
        return importFromText(examples.get(index).getText());
    }

    public Befunge93State importFromText(String text) {
        Befunge93State s = new Befunge93State();
        s.playfield.load(text.split("\\r?\\n"));
        return s;
    }

    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    private static final String[][] properties = {
        {"Author", "Chris Pressey"},
        {"Implementer", "Chris Pressey"},
        {"Implementation notes",
         "Reading integers from input (&) does not have the same semantics " +
         "as the 'bef' interpreter, but those semantics (naively " +
         "relying on C's scanf) are pretty crappy anyway, and we figure that " +
         "any Befunge-93 program that needs to read integers reliably will " +
         "read them character by character anyway.  In this implementation, & " +
         "reads digits until the first non-digit, which it consumes.  If there " +
         "are no digits at all, it consumes a character and pushes zero."},
    };

    public String[][] getProperties() {
        return properties;
    }

}

class Befunge93Playfield extends BasicPlayfield<CharacterElement> {
    protected WrapCursor<CharacterElement> pc = null;

    public Befunge93Playfield() {
        super(new CharacterElement(' '));
        clear();
    }

    public void clear() {
        super.clear();
        pc = new WrapCursor<CharacterElement>(this, IntegerElement.ZERO, IntegerElement.ZERO, IntegerElement.ONE, IntegerElement.ZERO);
    }

    public Befunge93Playfield clone() {
        Befunge93Playfield c = new Befunge93Playfield();
        c.copyBackingStoreFrom(this);
        c.pc = pc.clone();
        c.pc.setPlayfield(c);
        return c;
    }

    public IntegerElement getMinX() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMaxX() {
        return new IntegerElement(79);
    }

    public IntegerElement getMinY() {
        return IntegerElement.ZERO;
    }

    public IntegerElement getMaxY() {
        return new IntegerElement(24);
    }

    public int numCursors() {
        return 1;
    }

    public BasicCursor<CharacterElement> getCursor(int index) {
        if (index == 0)
            return pc;
        return null;
    }

    public void loadChar(int x, int y, char c) {
        if (x < 0 || x > 79 || y < 0 || y > 24) {
            return;
        }
        set(x, y, new CharacterElement(c));
    }
    
    public String dumpElement(CharacterElement e) {
        return e.getName();
    }
}

class Befunge93PlayfieldView extends BasicPlayfieldView {
}

public class Befunge93State implements State {
    protected BasicStack<Int32Element> stack;
    protected Befunge93Playfield playfield;
    protected BasicTapeView stackView;
    protected Befunge93PlayfieldView pfView;
    protected boolean stringmode = false;
    protected boolean halted = false;
    protected boolean needsInput = false;
    protected Int32Element inputIntAcc = null;
    private static Random rand = new Random();
    private static final Befunge93 language = new Befunge93();

    public Befunge93State() {
        stack = new BasicStack<Int32Element>(new Int32Element(0));
        playfield = new Befunge93Playfield();
        pfView = new Befunge93PlayfieldView();
        stackView = new BasicTapeView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public Befunge93State clone() {
        Befunge93State c = new Befunge93State();
        c.playfield = playfield.clone();
        c.stack = stack.clone();
        c.stringmode = stringmode;
        c.halted = halted;
        c.needsInput = needsInput;
        c.inputIntAcc = inputIntAcc;
        return c;
    }

    /*
     * This has some limitations
     */
    private boolean readDigits(World world) {
        for (;;) {
            CharacterElement c = world.inputCharacter();
            if (c == null) {
                return false;
            } else if (c.isDigit()) {
                inputIntAcc = new Int32Element(inputIntAcc.getValue() * 10 + c.digitValue());
            } else {
                stack.push(inputIntAcc);
                inputIntAcc = null;
                return true;
            }
        }
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<CharacterElement> pc = playfield.getCursor(0);
        CharacterElement i = pc.get();
        char instruction = i.getChar();
        Int32Element a, b;
        CharacterElement c;

        if (inputIntAcc != null) {
            boolean finished = readDigits(world);
            if (finished) {
                pc.advance();
                needsInput = false;
            } else {
                needsInput = true;
            }
            return errors;
        }

        if (stringmode) {
            if (instruction == '"') {
                stringmode = false;
            } else {
                stack.push(new Int32Element(instruction));
            }
        } else if (instruction >= '0' && instruction <= '9') {
            stack.push(new Int32Element((int)instruction - (int)'0'));
        } else switch (instruction) {
            case '+':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.add(b));
                break;
            case '-':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.subtract(b));
                break;
            case '*':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.multiply(b));
                break;
            case '/':
                b = stack.pop();
                a = stack.pop();
                if (b.isZero())
                    stack.push(Int32Element.ZERO);
                else
                    stack.push(a.divide(b));
                break;
            case '%':
                b = stack.pop();
                a = stack.pop();
                if (b.isZero())
                    stack.push(Int32Element.ZERO);
                else
                    stack.push(a.modulo(b));
                break;
            case '<':
                pc.setDelta(-1, 0);
                break;
            case '>':
                pc.setDelta(1, 0);
                break;
            case '^':
                pc.setDelta(0, -1);
                break;
            case 'v':
                pc.setDelta(0, 1);
                break;
            case '~':
                // ~ - Input an ASCII character from standard input and push onto stack.
                c = world.inputCharacter();
                if (c == null) {
                    needsInput = true;
                    return errors;
                }
                stack.push(new Int32Element(c.getChar()));
                break;
            case '&':
                // & - Input an integer (in ASCII characters, terminated by a non-digit)
                // from standard input and push onto stack.
                inputIntAcc = new Int32Element(0);
                boolean finished = readDigits(world);
                if (!finished) {
                    needsInput = true;
                    return errors;
                }
                break;
            case ',':
                // , - Pop a value off the stack and output as an ASCII character
                a = stack.pop();
                world.output(new CharacterElement(a.toChar()));
                break;
            case '.':
                // . - Pop a value off the stack and output as an decimal
                // integer followed by a space (all in ASCII)
                a = stack.pop();
                world.output(a);
                world.output(new CharacterElement(' '));
                break;
            case '#':
                // # - Jump over the next cell.
                pc.advance();
                break;
            case '@':
                // @ - End the program.
                halted = true;
                return errors;
            case '|':
                a = stack.pop();
                pc.setDelta(0, a.isZero() ? 1 : -1);
                break;
            case '_':
                a = stack.pop();
                pc.setDelta(a.isZero() ? 1 : -1, 0);
                break;
            case '$':
                a = stack.pop();
                break;
            case ':':
                a = stack.pop();
                stack.push(a);
                stack.push(a);
                break;
            case '\\':
                a = stack.pop();
                b = stack.pop();
                stack.push(a);
                stack.push(b);
                break;
            case '!':
                a = stack.pop();
                if (a.isZero())
                    stack.push(Int32Element.ONE);
                else
                    stack.push(Int32Element.ZERO);
                break;
            case '`':
                b = stack.pop();
                a = stack.pop();
                stack.push(a.getValue() > b.getValue() ?
                           Int32Element.ONE : Int32Element.ZERO);
                break;
            case '"':
                stringmode = true;
                break;
            case '?':
                switch (rand.nextInt(4)) {
                    case 0:
                        pc.setDelta(0, -1); break;
                    case 1:
                        pc.setDelta(0, 1); break;
                    case 2:
                        pc.setDelta(-1, 0); break;
                    case 3:
                        pc.setDelta(1, 0); break;
                }
                break;
            case 'g':
                b = stack.pop();
                a = stack.pop();
                c = playfield.get(a.getValue(), b.getValue());
                stack.push(new Int32Element((int)c.getChar()));
                break;
            case 'p':
                b = stack.pop();
                a = stack.pop();
                Int32Element v = stack.pop();
                c = new CharacterElement(v.getValue());
                playfield.set(a.getValue(), b.getValue(), c);
                break;
            default:
                // NOP
                break;
        }

        pc.advance();
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
            return stack;
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
        return stackView;
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
