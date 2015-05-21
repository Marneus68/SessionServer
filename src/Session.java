import java.util.HashMap;

/**
 * File created by duane
 * 2015-05-21 | 11:44 AM
 */

public class Session implements ISession {

    HashMap<String, String> content;

    public String id = "";
    public int received = 0;
    public int timeout = 0;

    Session(String i, int r, int t) {
        id = i;
        received = r;
        timeout = t;
        content = new HashMap<String, String>();
    }

    @Override
    public void setAttribute(String key, String obj) {
        content.put(key, obj);
    }

    @Override
    public String getAttribute(String key) {
        if (content.containsKey(key)) {
            return content.get(key);
        }
        return null;
    }
}
