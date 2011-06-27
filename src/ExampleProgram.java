/*
 * An ExampleProgram gives an example program for a particular
 * Language.  It describes the static properties of the example
 * program.  It supports loading the example program from a URL.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.io.InputStream;
import java.io.IOException;
import java.net.URL;
import tc.catseye.yoob.Error; // not Java's Error...

public class ExampleProgram {
    protected String name = null;
    protected String text = null;
    protected URL url = null;

    public ExampleProgram(String name, String text) {
        this.name = name;
        this.text = text;
    }

    public ExampleProgram(String name, URL url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getText() {
        if (text == null && url != null) {
            fetchProgramText();
        }
        return text;
    }

    protected void fetchProgramText() {
        int c;
        StringBuffer buffer = new StringBuffer();
        try {
            InputStream is = url.openStream();
            while ((c = is.read()) != -1) {
                buffer.append((char)c);
            }
        } catch (IOException e) {
            // we should complain, somehow, here
        }
        text = buffer.toString();
    }
}

