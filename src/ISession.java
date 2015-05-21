/**
 * File created by duane
 * 2015-05-21 | 11:46 AM
 */
public interface ISession {
    void setAttribute(String key, Object content);
    Object getAttribute(String key);
}
