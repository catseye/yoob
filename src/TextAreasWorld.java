/*
 * A World which hooks up to a pair of TextAreas for I/O.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import javax.swing.*;

public class TextAreasWorld implements World {
    private JTextArea inputTextArea, outputTextArea;

    public void setInputTextArea(JTextArea inputTextArea) {
       this.inputTextArea = inputTextArea;
    }

    public void setOutputTextArea(JTextArea outputTextArea) {
       this.outputTextArea = outputTextArea;
    }

    public void output(Element e) {
        if (outputTextArea == null) return;
        outputTextArea.append(e.getName());
        outputTextArea.setCaretPosition(
          outputTextArea.getDocument().getLength()
        );
    }

    public CharacterElement inputCharacter() {
        if (inputTextArea == null) return null;
        // pop from front of input textarea
        String s = inputTextArea.getText();
        if (s.length() > 0) {
            CharacterElement c = new CharacterElement(s.charAt(0));
            inputTextArea.setText(s.substring(1));
            return c;
        }
        return null;
    }
}
