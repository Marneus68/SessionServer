import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

/**
 * File created by duane
 * 2015-05-21 | 11:12 AM
 */

public class SessionServer {
    public static final String CRLF = "\r\n";
    public static final int PORT = 8989;

    public static final HashMap<String, Session> sessions = new HashMap<String, Session>();
    private static int counter;

    public static void main(String [] args) throws Exception {
        int port = PORT;

        ServerSocket ss = null;
        try {
            ss = new ServerSocket(port);
        } catch (Exception e) {
            System.err.println("Unable to start server (" + e.getMessage() + ")");
            System.exit(0);
        }
        System.out.println("SessionServer started on port " + port + ".");
        try {
            while (true) {
                Socket s = ss.accept();
                System.out.println(" -> New connection accepted [" + s.getInetAddress() + ":" + s.getPort() + "]");
                /*
                    Create new session:
                        SNEW:120
                    Set session attribute
                        SSET:1234:ObjectKey:[object]
                    Get session attribute
                        SGET:1234:ObjectKey
                    Kill session
                        SKILL:1234
                 */
                try {
                    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
                    OutputStream os = s.getOutputStream();
                    BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    String line = null;

                    while ((line = in.readLine()) != null && !line.isEmpty()) {
                        String [] sline = line.split(":");
                        String  opcode = sline[0],
                                id = "",
                                key = "",
                                timeout = "";

                        if (opcode.equals("SNEW")) {
                            timeout = sline[1];
                            id = String.valueOf(counter);
                            counter++;
                            long ltimestamp = System.currentTimeMillis() / 1000;
                            String timestamp = String.valueOf(ltimestamp);
                            timeout = sline[1];
                            System.out.println("    Creating new session with id=" + id + " at " + timestamp + " with timeout=" + timeout + "s");
                            sessions.put(id, new Session(id, Integer.valueOf(timestamp), Integer.valueOf(timeout)));

                            /* sending out a reply containing ONLY the session ID */
                            os.write(id.getBytes());
                            os.write(CRLF.getBytes());

                        } else if (opcode.equals("SSET")) {
                            id = sline[1];
                            key = sline[2];
                            String object = sline[3];
                            System.out.println("    Setting attribute \"" + key + "\" for session " + id);
                            if (sessions.containsKey(id)) {
                                sessions.get(id).setAttribute(key, object);
                            } else {
                                System.err.println("    No session with id " + id);
                            }
                        } else if (opcode.equals("SGET")) {
                            id = sline[1];
                            key = sline[2];
                            String object;
                            System.out.println("    Getting attribute \"" + key + "\" for session " + id);
                            if (sessions.containsKey(id)) {
                                if ((object = sessions.get(id).getAttribute(key)) != null) {
                                    os.write(object.getBytes());
                                    os.write(CRLF.getBytes());
                                } else {
                                    System.err.println("    Session " + id + " doesn't contain any object at " + key);
                                }
                            } else {
                                System.err.println("    No session with id " + id);
                            }
                        } else if (opcode.equals("SKILL")) {
                            id = sline[1];
                            System.out.println("    Killing session " + id);
                            if (sessions.containsKey(id)) {
                                sessions.remove(id);
                            } else {
                                System.err.println("    No session with id " + id);
                            }
                        } else {
                            System.err.println("    Invalid command: " + line);
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
                s.close();
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            ss.close();
        }
    }
}