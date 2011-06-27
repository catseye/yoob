/*
 * A ProgramTextArea is a JTextArea which supports niceties for displaying
 * the program text of a text-based esolang.  Specifically, it supports
 * highlighting the current area of the program being executed.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;

public class ProgramTextArea extends JTextArea {
    public void highlightPosition(int position) {
        // highlight all characters that appear in charsToHighlight
        Highlighter h = getHighlighter();
        h.removeAllHighlights();
        try {
            h.addHighlight(position, position + 1,
                           DefaultHighlighter.DefaultPainter);
        } catch (BadLocationException ble) {
            // oh well
        }
    }
}
