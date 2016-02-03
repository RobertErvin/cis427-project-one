package projectPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import projectPackage.Constants.CMDS;
import projectPackage.Constants.RESPONSES;

public class SocketServer {
	/*
	 * The view for the socket. This handles
	 * incoming requests and outgoing responses.
	 */
    private static Boolean shutdown;
    private static Socket socket;

    // Initialize the socket and start listening
    public static void main(String[] args) throws Exception {
        System.out.println("The server is running...");
        shutdown = false;
        ServerSocket listener = new ServerSocket(9898);
        try {
            while (true) {
                socket = listener.accept();
                new CIS427SocketServer(socket).start();
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
        private BufferedReader in;
        private PrintWriter out;

        // Initialize the variables
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

        // Listen for the requests and handle them. Send corresponding responses.
        public void run() {
            try {
                in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String input = in.readLine();
                    log(input);
                    // log(motdService.getNext());
                    if (isMsgStore) { // Store a message
                        if (user.isLoggedIn()) {
                            try {
                                motdService.add(input);
                                isMsgStore = false;
                                out.println(RESPONSES.OK.toString());
                            } catch (Exception e) {
                                log(e.getMessage());
                            }
                        } else {
                            out.println(RESPONSES.LOGIN_ERROR.toString());
                        }
                    } else if (input.equals(CMDS.MSGGET.toString())) { // Request to store a message
                        String motd = motdService.getNext();
                        out.println(RESPONSES.OK.toString());
                        out.println(motd);
                    } else if (input.equals(CMDS.MSGSTORE.toString())) {
                        if (user.isLoggedIn()) {
                            isMsgStore = true;
                            out.println(RESPONSES.OK.toString());
                        } else {
                            out.println(RESPONSES.LOGIN_ERROR.toString());
                        }
                    } else if (input.contains(CMDS.LOGIN.toString())) { // Login
                        try {
                            System.out.println("Login");
                            user = userService.parseUserAuthData(input);
                            user = userService.authorize(user);
                            out.println(RESPONSES.OK.toString());
                        } catch (IllegalArgumentException e) {
                            out.println(RESPONSES.BAD_USERNAME_OR_PASSWORD_ERROR.toString());
                        } catch (IllegalAccessError e) {
                            out.println(RESPONSES.LOGOUT_ERROR.toString());
                        }
                    } else if (input.equals(CMDS.LOGOUT.toString())) { // Logout
                        try {
                            user = userService.logout(user);
                            out.println(RESPONSES.OK.toString());
                        } catch (IllegalAccessError e) {
                            out.println(RESPONSES.LOGIN_ERROR.toString());
                        }
                    } else if (input.equals(CMDS.QUIT.toString())) { // Quit
                        out.println(RESPONSES.OK.toString());
                        break;
                    } else if (input.equals(CMDS.SHUTDOWN.toString())) { // Shutdown
                        if (user.isRoot()) {
                            try {
                                shutdown();
                                shutdown = true;
                                break;
                            } catch (Exception e) {
                                out.println(RESPONSES.FORMAT_ERROR.toString());
                            }
                        } else {
                            out.println(RESPONSES.BAD_PERMISSIONS_ERROR.toString());
                        }
                    }
                }
            } catch (IOException e) { // Something bad happened
                log("Error handling client: " + e);
            } finally { // close stuff
                try {
                    socket.close();
                } catch (IOException e) {
                    log("Couldn't close a socket.");
                }
                log("Connection with client closed");
            }
        }

        private void shutdown() throws Exception {
            out.println(RESPONSES.OK.toString());
            out.close();
            in.close();
            socket.close();
        }

        public static void log(String message) {
            System.out.println(message);
        }
    }
}
