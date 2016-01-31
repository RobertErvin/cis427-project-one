package projectPackage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import projectPackage.Constants.CMDS;

public class SocketServer {
	/*
	 * The view for the socket. This handles
	 * incoming requests and outgoing responses.
	 */

    // Initialize the socket and start listening
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
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

                while (true) {
                    String input = in.readLine();
                    if (isMsgStore) { // Store a message
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
                    } else if (input.equals(CMDS.MSGGET.toString())) { // Request to store a message
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
                    } else if (input.contains(CMDS.LOGIN.toString())) { // Login
                        try {
                            user = userService.parseUserAuthData(input);
                            user = userService.authorize(user);
                            out.println("200 OK");
                        } catch (IllegalArgumentException e) {
                            out.println("410 Wrong UserID or Password");
                        } catch (IllegalAccessError e) {
                            out.println("401 You are already logged in, logout first.");
                        }
                    } else if (input.equals(CMDS.LOGOUT.toString())) { // Logout
                        try {
                            user = userService.logout(user);
                            out.println("200 OK");
                        } catch (IllegalAccessError e) {
                            out.println("401 You are not currently logged in, login first.");
                        }
                    } else if (input.equals(CMDS.QUIT.toString())) { // Quit
                        out.println("200 OK");
                        break;
                    } else if (input.equals(CMDS.SHUTDOWN.toString())) { // Shutdown
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
            socket.close();
        }

        private void log(String message) {
            System.out.println(message);
        }
    }
}
