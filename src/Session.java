import java.util.HashMap;

/**
 * File created by duane
 * 2015-05-21 | 11:44 AM
 */
public class Session implements ISession {

    HashMap<String, Object> content;

    public String id = "";
    public int received = 0;
    public int timeout = 0;

    Session(String i, int r, int t) {
        id = i;
        received = r;
        timeout = t;
        content = new HashMap<String, Object>();
    }

    @Override
    public void setAttribute(String key, Object content) {
        //content.add(key, content);
    }

    @Override
    public Object getAttribute(String key) {
        if (content.containsKey(key)) {
            return content.get(key);
        }
        return null;
    }
}
