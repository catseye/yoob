/*
 * A CommonPlayfield inherits from BasicPlayfield, and adds semantics
 * to it that are common to many 2-dimensional languages.  Mainly this
 * is specifying that the playfield contains characters, by default blank,
 * and has a single BasicCursor called the IP (Instruction Pointer).
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.Map;
import java.util.HashMap;

public class CommonPlayfield extends BasicPlayfield<CharacterElement> {
    protected BasicCursor<CharacterElement> ip = null;

    public CommonPlayfield() {
        super(new CharacterElement(' '));
        clear();
    }

    public void clear() {
        super.clear();
        ip = new BasicCursor<CharacterElement>(this, IntegerElement.ZERO, IntegerElement.ZERO, IntegerElement.ONE, IntegerElement.ZERO);
    }

    public CommonPlayfield clone() {
        CommonPlayfield c = new CommonPlayfield();
        c.copyBackingStoreFrom(this);
        c.ip = ip.clone();
        c.ip.setPlayfield(c);
        return c;
    }

    public int numCursors() {
        return 1;
    }

    public BasicCursor<CharacterElement> getCursor(int index) {
        if (index == 0)
            return ip;
        return null;
    }

    public void loadChar(int x, int y, char c) {
        set(x, y, new CharacterElement(c));
    }

    public boolean hasFallenOffEdge() {
       return hasFallenOffEdge(ip);
    }
}
