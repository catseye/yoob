/*
 * A SMETANAState implements the semantics of SMETANA.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob.smetana;

import tc.catseye.yoob.*;
import tc.catseye.yoob.Error;

import java.util.ArrayList;
import java.util.List;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.awt.Component;


abstract class SMETANAInstruction implements Element {
      protected int position;
      public String getName() {
          return position + ". " + getInstructionName();
      }
      public abstract String getInstructionName();
      public abstract boolean equals(Element e); // this should disregard position
      public Element fromChar(char c) {
          return null;
      }
      // returns false to mean illegal
      public abstract boolean execute(BasicTape<SMETANAInstruction> tape);
}

class SwapInstruction extends SMETANAInstruction {
      protected int stepA, stepB; // these are ONE-BASED

      public SwapInstruction(int position, int stepA, int stepB) {
          this.position = position;
          this.stepA = stepA;
          this.stepB = stepB;
      }

      public String getInstructionName() {
          return "Swap step " + stepA + " with step " + stepB + ".";
      }

      public boolean equals(Element e) {
          if (!(e instanceof SwapInstruction)) return false;
          SwapInstruction other = (SwapInstruction)e;
          return (stepA == other.stepA && stepB == other.stepB);
      }

      public boolean execute(BasicTape<SMETANAInstruction> tape) {
          int max = tape.getMax().intValue();
          if (stepA-1 < 0 || stepA-1 > max) return false;
          if (stepB-1 < 0 || stepB-1 > max) return false;
          SMETANAInstruction a = tape.read(stepA-1);
          SMETANAInstruction b = tape.read(stepB-1);
          a.position = stepB;
          b.position = stepA;
          tape.write(stepA-1, b);
          tape.write(stepB-1, a);
          return true;
      }
}

class GotoInstruction extends SMETANAInstruction {
      protected int step; // this is ONE-BASED

      public GotoInstruction(int position, int step) {
          this.position = position;
          this.step = step;
      }

      public String getInstructionName() {
          return "Go to step " + step + ".";
      }
      
      public boolean equals(Element e) {
          if (!(e instanceof GotoInstruction)) return false;
          GotoInstruction other = (GotoInstruction)e;
          return (step == other.step);
      }

      public boolean execute(BasicTape<SMETANAInstruction> tape) {
          int max = tape.getMax().intValue();
          if (step-1 < 0 || step-1 > max) return false;
          // -1 for being one-based
          // and another -1 because we advance immediately thereafter
          tape.getHead(0).setPos(step-2);
          return true;
      }
}

class SMETANA implements Language {
    public String getName() {
        return "SMETANA";
    }

    public int numPlayfields() {
        return 0;
    }

    public int numTapes() {
        return 1;
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
        names.add("It'll never fly");
        return names;
    }

    public SMETANAState loadExampleProgram(int index) {
        // Example made up by Chris Pressey, placed in the public domain.
        String[] program = {
            "Swap step 2 with step 1.\n" +
            "Go to step 4.\n" +
            "Go to step 1.\n",
        };
        SMETANAState s = importFromText(program[index]);
        return s;
    }

    public SMETANAState importFromText(String text) {
        SMETANAState s = new SMETANAState();
        String lines[] = text.split("\\r?\\n");
        Pattern gotoPattern = Pattern.compile("^\\s*Go\\s*to\\s*step\\s*(\\d+)\\s*\\.\\s*$");
        Pattern swapPattern = Pattern.compile("^\\s*Swap\\s*step\\s*(\\d+)\\s*with\\s*step\\s*(\\d+)\\s*\\.\\s*$");
        int pos = 0;
        for (String line : lines) {
            SMETANAInstruction i = null;
            Matcher gotoMatcher = gotoPattern.matcher(line);
            if (gotoMatcher.matches()) {
                i = new GotoInstruction(pos+1, Integer.parseInt(gotoMatcher.group(1)));
            }
            Matcher swapMatcher = swapPattern.matcher(line);
            if (swapMatcher.matches()) {
                i = new SwapInstruction(pos+1, Integer.parseInt(swapMatcher.group(1)),
                                        Integer.parseInt(swapMatcher.group(2)));
            }
            if (i != null) {
                s.tape.write(pos, i);
                ++pos;
            }
        }
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
         "If any attempt is made to go to or to swap an instruction which is " +
         "outside the bounds of the program, execution will halt."},
    };

    public String[][] getProperties() {
        return properties;
    }
}

class SMETANAProgramView extends BasicTapeView {
    public int getPreferredCellWidth() {
        return 40;
    }

    public float getAlignmentX() {
        return Component.LEFT_ALIGNMENT;
    }
}

public class SMETANAState implements State {
    protected BasicTape<SMETANAInstruction> tape;
    protected SMETANAProgramView tapeView;
    protected boolean halted = false;
    private static final SMETANA language = new SMETANA();

    public SMETANAState() {
        tape = new BasicTape<SMETANAInstruction>(null);
        tapeView = new SMETANAProgramView();
    }
    
    public Language getLanguage() {
        return language;
    }

    public SMETANAState clone() {
        SMETANAState c = new SMETANAState();
        c.tape = tape.clone();
        c.halted = halted;
        return c;
    }

    public List<Error> step(World world) {
        ArrayList<Error> errors = new ArrayList<Error>();
        BasicHead<SMETANAInstruction> h = tape.getHead(0);
        SMETANAInstruction i = h.read();
        if (!i.execute(tape)) {
            halted = true;
            return errors;
        }
        h.move(1);
        if (h.getPos().compareTo(tape.getMax()) > 0) {
            halted = true;
        }
        return errors;
    }

    public Playfield getPlayfield(int index) {
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
        return null;
    }

    public View getTapeView(int index) {
        if (index == 0)
            return tapeView;
        return null;
    }

    public String exportToText() {
        StringBuffer result = new StringBuffer();
        for (IntegerElement k = IntegerElement.ZERO;
            k.compareTo(tape.getMax()) <= 0;
            k = k.succ()) {
            result.append(tape.read(k).getInstructionName());
            result.append("\n");   
        }
        return result.toString();
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
