/*
 * A GemooyState implements the semantics of Gemooy.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.gemooy;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import java.awt.Graphics;
import java.awt.Color;


class Gemooy implements Language {
    public String getName() {
        return "Gemooy";
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
        names.add("grow indefinitely");
        names.add("toggle bounded column");
        names.add("solid - turn around - dotted");
        return names;
    }

    public GemooyState loadExampleProgram(int index) {
        // All examples from: http://www.esolangs.org/wiki/Gemooy
        // By Chris Pressey.  From the esowiki, thus in the public domain.
        String[][] program = {
          {
            " @@ %",
            "@  $",
            "@   #",
            "     #",
            "      @# @",
            "          @",
            "          @",
            "     @   @"
          },
          {
            "%   @@   @@",
            "#  @  $    @",
            "       @    ",
            "#      #    ",
            "#           ",
            "       @    ",
            "#     # #   ",
            "#    #      ",
            "    @     @ ",
            "    @     @@",
            "#  @ @    @",
            "@   @   @",
            ""
          },
          {
            "%           @",
            "",
            "",
            " @@      @@",
            "@       @  @",
            "@          @",
            " @   $    @",
            "      #",
            "       #",
            "        @  # @#  @",
            "              @   @",
            "              @   #",
            "           @ @",
            "",
            "            @@             @@",
            "           @              @  @",
            "           @                 @",
            "            @               @",
            "                 #",
            "                  @  #@#    @",
            "                 #     @     @",
            "                #         @  @",
            "               @           @@",
            "               @       @",
            "                @     @",
          }

        };
        GemooyState s = new GemooyState();
        s.playfield.load(program[index]);
        return s;
    }

    public GemooyState importFromText(String text) {
        GemooyState s = new GemooyState();
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
    };

    public String[][] getProperties() {
        return properties;
    }
}

class GemooyElement implements Element {
    private String symbol;
    public GemooyElement next;

    static public GemooyElement BLANK = null;
    static public GemooyElement HASH = null;
    static public GemooyElement AT = null;

    public GemooyElement(String symbol) {
        this.symbol = symbol;
    }
    
    public String getName() {
        return symbol;
    }

    public boolean equals(Element e) {
        if (e instanceof GemooyElement) {
            return this == e;
        }
        return false;
    }

    public GemooyElement fromChar(char c) {
        init();
        if (c == '#')
            return HASH;
        else if (c == '@')
            return AT;
        else
            return BLANK;
    }

    public static void init() {
        if (BLANK != null) return;
        BLANK = new GemooyElement(" ");
        HASH = new GemooyElement("#");
        AT = new GemooyElement("@");
        BLANK.next = HASH;
        HASH.next = AT;
        AT.next = BLANK;
    }
}

class GemooyPlayfield extends BasicPlayfield<GemooyElement> {
    protected BasicCursor<GemooyElement> ip = null;
    protected BasicCursor<GemooyElement> dp = null;

    // You should call GemooyElement.init() before creating one of these!
    public GemooyPlayfield() {
        super(GemooyElement.BLANK);
        clear();
    }

    public void clear() {
        super.clear();
        ip = new BasicCursor<GemooyElement>(this, IntegerElement.ZERO, IntegerElement.ZERO, IntegerElement.ONE, IntegerElement.ONE);
        dp = new BasicCursor<GemooyElement>(this);
    }

    public GemooyPlayfield clone() {
        GemooyPlayfield c = new GemooyPlayfield();
        c.copyBackingStoreFrom(this);
        c.ip = ip.clone();
        c.ip.setPlayfield(c);
        c.dp = dp.clone();
        c.dp.setPlayfield(c);
        return c;
    }

    public int numCursors() {
        return 2;
    }

    public BasicCursor<GemooyElement> getCursor(int index) {
        if (index == 0)
            return ip;
        if (index == 1)
            return dp;
        return null;
    }

    public void loadChar(int x, int y, char c) {
        GemooyElement e = GemooyElement.BLANK.fromChar(c);
        switch (c) {
            case '$':
                ip.setX(x);
                ip.setY(y);
                break;
            case '%':
                dp.setX(x);
                dp.setY(y);
                break;
        }
        set(x, y, e);
    }

    /*
     * Overrides version in BasicPlayfield.
     */
    public String dump() {
        IntegerElement min_x = getMinX();
        IntegerElement min_y = getMinY();
        IntegerElement max_x = getMaxX();
        IntegerElement max_y = getMaxY();
        IntegerElement x = min_x;
        IntegerElement y = min_y;
        StringBuffer buf = new StringBuffer();
        int blanks;

        while (y.compareTo(max_y) <= 0) {
            x = min_x;
            blanks = 0;
            while (x.compareTo(max_x) <= 0) {
                if (x.compareTo(ip.getX()) == 0 && y.compareTo(ip.getY()) == 0) {
                    while (blanks > 0) {
                        buf.append(GemooyElement.BLANK.getName());
                        blanks--;
                    }
                    buf.append('$');
                } else if (x.compareTo(dp.getX()) == 0 && y.compareTo(dp.getY()) == 0) {
                    while (blanks > 0) {
                        buf.append(GemooyElement.BLANK.getName());
                        blanks--;
                    }
                    buf.append('%');
                } else {
                    Element e = get(x, y);
                    if (e == GemooyElement.BLANK) {
                        blanks++;
                    } else {
                        while (blanks > 0) {
                            buf.append(GemooyElement.BLANK.getName());
                            blanks--;
                        }
                        buf.append(e.getName());
                    }
                }
                x = x.succ();
            }
            y = y.succ();
            buf.append("\n");
        }

        return buf.toString();
    }
}

class GemooyPlayfieldView extends BasicPlayfieldView {
    public void render(Graphics g, Cursor c, int x, int y, int w, int h) {
        Playfield p = c.getPlayfield();
        if (c == p.getCursor(0)) {
            g.setColor(Color.red);
        } else {
            g.setColor(Color.green);
        }
        g.drawRoundRect(x - 1, y - 1, w + 2, h + 2, w / 4, h / 4);
    }
}

public class GemooyState implements State {
    protected GemooyPlayfield playfield;
    protected GemooyPlayfieldView view;
    protected boolean halted = false;
    private static final Gemooy language = new Gemooy();

    public GemooyState() {
        GemooyElement.init();
        playfield = new GemooyPlayfield();
        view = new GemooyPlayfieldView();
    }

    public Language getLanguage() {
        return language;
    }
    
    public GemooyState clone() {
        GemooyState c = new GemooyState();
        c.playfield = this.playfield.clone();
        c.halted = halted;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicCursor<GemooyElement> ip = playfield.getCursor(0);
        BasicCursor<GemooyElement> dp = playfield.getCursor(1);

        Element instruction = ip.get();

        if (instruction == GemooyElement.BLANK) {
            /* no effect */
        } else if (instruction == GemooyElement.HASH) {
            if (ip.isHeaded(0, -1)) {
                // * North = Move data pointer one cell north, skip instruction pointer over next cell.
                dp.move(0, -1);
                ip.advance();
            } else if (ip.isHeaded(0, 1)) {
                // * South = Move data pointer one cell south, skip instruction pointer over next cell.
                dp.move(0, 1);
                ip.advance();
            } else if (ip.isHeaded(1, 0)) {
                // * East = Move data pointer one cell east, skip instruction pointer over next cell.
                dp.move(1, 0);
                ip.advance();
            } else if (ip.isHeaded(-1, 0)) {
                // * West = Move data pointer one cell west, skip instruction pointer over next cell.
                dp.move(-1, 0);
                ip.advance();
            } else if (ip.isHeaded(-1, -1) || ip.isHeaded(1, -1)) {
                // * Northeast or Northwest = Increment cell at data pointer.
                GemooyElement datum = (GemooyElement)dp.get();
                dp.set(datum.next);
            } else if (ip.isHeaded(-1, 1) || ip.isHeaded(1, 1)) {
                // * Southeast or Southwest = Decrement cell at data pointer. 
                GemooyElement datum = (GemooyElement)dp.get();
                dp.set(datum.next.next);
            } else {
                // TODO: add error to errors
            }
        } else if (instruction == GemooyElement.AT) {
            GemooyElement datum = (GemooyElement)dp.get();
            if (datum == GemooyElement.HASH) {
                ip.rotate(-45);
            } else if (datum == GemooyElement.AT) {
                /* no effect */
            } else if (datum == GemooyElement.BLANK) {
                ip.rotate(45);
            } else {
                // TODO: add error to errors
            }
        } else {
            // TODO: add error to errors
        }

        ip.advance();
        if (playfield.hasFallenOffEdge(ip)) {
            halted = true;
        }

        return errors;
    }

    public Playfield getPlayfield(int index) {
        return playfield;
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
        return view;
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
    
    public List<String> getAvailableOptionNames() {
        ArrayList<String> names = new ArrayList<String>();
        return names;
    }

    public void setOption(String name, boolean value) {
    }
}
