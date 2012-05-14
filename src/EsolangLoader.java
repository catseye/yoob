/*
 * An EsolangLoader encapsulates the mechanics of loading
 * the Java classes which implement a particular yoob language.
 * The source code in this file has been placed into the public domain.
 */
package tc.catseye.yoob;

import java.lang.InstantiationException;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class EsolangLoader {
    private Map<String, String> classNameMap = new HashMap<String, String>();
    private Map<String, State> stateMap = new HashMap<String, State>();
    private List<String> regrets = new ArrayList<String>();

    public void load(String allNames) {
        String[] names = allNames.split("\\s+");
        for (String name : names) {
            String[] pair = name.split("/");
            classNameMap.put(pair[1], pair[0]);
        }
    }

    public List<String> getNames() {
        List<String> names = new ArrayList<String>(classNameMap.keySet());
        Collections.sort(names);
        return names;
    }

    public State getState(String name) {
        State w = stateMap.get(name);
        if (w == null) {
            try {
                String className = classNameMap.get(name);
                Class c = Class.forName(className);
                if (!State.class.isAssignableFrom(c))
                    throw new InstantiationException("Class " + className + " is not a tc.catseye.yoob.State");
                State state = (State)c.newInstance();
                if (state.getLanguage() != null) {
                    stateMap.put(name, state);
                    w = state;
                }
            } catch (InstantiationException x) {
                x.printStackTrace();
		regrets.add(x.getMessage());
            } catch (IllegalAccessException x) {
                x.printStackTrace();
		regrets.add(x.getMessage());
            } catch (ClassNotFoundException x) {
                x.printStackTrace();
		regrets.add(x.getMessage());
            } catch (ClassCastException x) {
                x.printStackTrace();
		regrets.add(x.getMessage());
            }
        }
        return w;
    }
}
