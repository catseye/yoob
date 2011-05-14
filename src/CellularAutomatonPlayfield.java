/*
 * A CellularAutomatonPlayfield is a BasicPlayfield that provides measures
 * to make it easier to implement a cellular automaton.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public abstract class CellularAutomatonPlayfield<E extends Element> extends BasicPlayfield<E> {
    public CellularAutomatonPlayfield(E def) {
        super(def);
    }

    public int getNeighbours(IntegerElement x, IntegerElement y, Criterion<E> c) {
        int n = 0;
        if (c.qualifies(get(x.pred(), y.pred()))) n++;
        if (c.qualifies(get(x, y.pred())))        n++;
        if (c.qualifies(get(x.succ(), y.pred()))) n++;
        if (c.qualifies(get(x.pred(), y)))        n++;
        if (c.qualifies(get(x.succ(), y)))        n++;
        if (c.qualifies(get(x.pred(), y.succ()))) n++;
        if (c.qualifies(get(x, y.succ())))        n++;
        if (c.qualifies(get(x.succ(), y.succ()))) n++;
        return n;
    }

    public void step(CellularAutomatonPlayfield<E> nu) {
        for (IntegerElement x = getMinX().pred();
             x.compareTo(getMaxX().succ()) <= 0;
             x = x.succ()) {
            for (IntegerElement y = getMinY().pred();
                 y.compareTo(getMaxY().succ()) <= 0;
                 y = y.succ()) {
                E elem = get(x, y);
                E result = applyRules(x, y, elem);
                if (result != null) {
                    nu.set(x, y, result);
                }
            }
        }
    }

    /*
     * Inheriter should implement this to implement the rules that apply
     * for each cell in the cellular automaton.
     */
    public abstract E applyRules(IntegerElement x, IntegerElement y, E elem);
}
