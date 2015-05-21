import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * File created by duane
 * 2015-05-21 | 11:12 AM
 */
public class SessionServer {

    public static final int PORT = 8989;

    public static final HashMap<String, HashMap<String, Object>> sessionContent = new HashMap<String, HashMap<String, Object>>();

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
                            System.out.println("    Creating new session with id=" + "xxxx" + "and timeout=" + timeout);
                        } else if (opcode.equals("SSET")) {
                            id = sline[1];
                            key = sline[2];
                            System.out.println("    Setting attribute " + key + " for session " + id);
                        } else if (opcode.equals("SGET")) {
                            id = sline[1];
                            key = sline[2];
                            System.out.println("    Getting attribute " + key + " for session " + id);
                        } else if (opcode.equals("SKILL")) {
                            id = sline[1];
                            System.out.println("    Killing session " + id);
                        }
                    }
                } catch (Exception e) {
                    System.err.println(e);
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        } finally {
            ss.close();
        }
    }
}