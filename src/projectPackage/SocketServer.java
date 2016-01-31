package projectPackage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketServer {
    public enum CMDS {
        MSGGET,
        MSGSTORE,
        SHUTDOWN,
        LOGIN,
        LOGOUT,
        QUIT
    }

    public static void main(String[] args) throws Exception {
        System.out.println("The server is running...");

        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                new CIS427SocketServer(listener.accept()).start();
            }
        } finally {
            listener.close();
        }
    }

    private static class CIS427SocketServer extends Thread {
        private Socket socket;
        private User user;
        private UserService userService;
        private MotdService motdService;
        private boolean isMsgStore = false;

        public CIS427SocketServer(Socket socket) {
            this.socket = socket;
            this.user = new User();
            this.userService = new UserService();

            try {
                this.motdService = new MotdService();
            } catch (Exception e) {
                log(e.getMessage());
            }

            log("New connection with client at " + socket);
        }

        public void run() {
            try {
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String input = in.readLine();
                    if (isMsgStore) {
                        if (user.isLoggedIn()) {
                            try {
                                motdService.add(input);
                                isMsgStore = false;
                                out.println("200 OK");
                            } catch (Exception e) {
                                log(e.getMessage());
                            }
                        } else {
                            out.println("401 You are not currently logged in, login first.");
                        }
                    } else if (input.equals(CMDS.MSGGET.toString())) {
                        String motd = motdService.getNext();
                        out.println("200 OK");
                        out.println(motd);
                    } else if (input.equals(CMDS.MSGSTORE.toString())) {
                        if (user.isLoggedIn()) {
                            isMsgStore = true;
                            out.println("200 OK");
                        } else {
                            out.println("401 You are not currently logged in, login first.");
                        }
                    } else if (input.contains(CMDS.LOGIN.toString())) {
                        try {
                            user = parseUserAuthData(input);
                            user = userService.authorize(user);
                            out.println("200 OK");
                        } catch (IllegalArgumentException e) {
                            out.println("410 Wrong UserID or Password");
                        } catch (IllegalAccessError e) {
                            out.println("401 You are already logged in, logout first.");
                        }
                    } else if (input.equals(CMDS.LOGOUT.toString())) {
                        try {
                            user = userService.logout(user);
                            out.println("200 OK");
                        } catch (IllegalAccessError e) {
                            out.println("401 You are not currently logged in, login first.");
                        }
                    } else if (input.equals(CMDS.QUIT.toString())) {
                        out.println("200 OK");
                        break;
                    } else if (input.equals(CMDS.SHUTDOWN.toString())) {
                        if (user.isRoot()) {
                            try {
                                shutdown();
                            } catch (Exception e) {
                                out.println("300 message format error");
                            }
                        } else {
                            out.println("402 User not allowed to execute this command.");
                        }
                    }

                    out.println(input.toUpperCase());
                }
            } catch (IOException e) {
                log("Error handling client: " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket.");
                }
                log("Connection with client closed");
            }
        }

        private User parseUserAuthData(String data) {
            User user = new User();
            Pattern pattern = Pattern.compile("(LOGIN\\s(\\w+)\\s(\\w+))");
            Matcher matcher = pattern.matcher(data);
            if (!matcher.find() || matcher.groupCount() < 4) {
                throw new IllegalArgumentException();
            }

            String userId = matcher.group(1);
            String password = matcher.group(2);

            user.setId(userId);
            user.setPassword(password);

            return user;
        }

        private void shutdown() throws Exception {
            socket.close();
        }

        private void log(String message) {
            System.out.println(message);
        }
    }
}
