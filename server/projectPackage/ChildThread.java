package projectPackage;

/* 
 * ChildThread.java
 */

import java.io.*;
import java.net.Socket;
import java.util.Vector;

import projectPackage.Constants.CMDS;
import projectPackage.Constants.RESPONSES;

public class ChildThread extends Thread {
    static Vector<ChildThread> handlers = new Vector<ChildThread>(20);
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private User user;
    private UserService userService;
    private MotdService motdService;
    private boolean isMsgStore = false;
    private boolean isSendMsg = false;
    private String sendMsgUserId;

    public ChildThread(Socket socket) throws IOException {
    	this.socket = socket;
		in = new BufferedReader(
		    new InputStreamReader(socket.getInputStream()));
		out = new PrintWriter(
		    new OutputStreamWriter(socket.getOutputStream()));
    }

    public void run() {
		String input;
		synchronized(handlers) {
		    // add the new client in Vector class
		    handlers.addElement(this);
		}

		try {
		    while ((input = in.readLine()) != null) {
				log(input);
			
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
                } else if (isSendMsg) {
                	if (user.isLoggedIn()) {
                        try {
                        	for (int i = 0; i < handlers.size(); i++) {	
            				    synchronized(handlers) {
            						ChildThread handler = (ChildThread) handlers.elementAt(i);
            						
            						if (handler != this && handler.getUser().getId().equals(sendMsgUserId)) {
            							handler.out.println(RESPONSES.OK.toString() + " you have a new message from " + user.getId());
            						    handler.out.println(user.getId().toLowerCase() + ": " + input);
            						    handler.out.flush();
            						}
            				    }
            				}
                        	
                        	isSendMsg = false;
                        	sendMsgUserId = null;
                        	
                            out.println(RESPONSES.OK.toString());
                        } catch (Exception e) {
                            log(e.getMessage());
                        }
                    } else {
                        out.println(RESPONSES.LOGIN_ERROR.toString());
                    }
                } else if (input.equals(CMDS.WHO.toString())) { // Request to list authorized users
                    out.println(RESPONSES.OK.toString());
                    out.println("The list of active users");
                    
                    for (User user: userService.getAllActiveUsers()) {
                    	out.println(user.getId() + "\t" + user.getIpAddress());
                    }
                } else if (input.contains(CMDS.SEND.toString())) { // Request to send a message
                	String userId = userService.parseUserId(input);
                	
                	if (userService.isUserLoggedIn(userId)) {
                		sendMsgUserId = userId;
                		out.println(RESPONSES.OK.toString());
                	} else {
                		out.println(RESPONSES.INVALID_RECEIVER_ERROR.toString());
                	}
                } else if (input.equals(CMDS.MSGGET.toString())) { // Request to get a message
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
                        user = userService.parseUserAuthData(input, socket.getInetAddress().getHostAddress());
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
                } else if (input.equals(CMDS.SHUTDOWN.toString())) { // Shutdown
                    if (user.isLoggedIn() && user.isRoot()) {
                        try {
                            shutdown();
                            break;
                        } catch (Exception e) {
                            out.println(RESPONSES.FORMAT_ERROR.toString());
                        }
                    } else {
                        out.println(RESPONSES.BAD_PERMISSIONS_ERROR.toString());
                    }
                }
                
                out.println("exit");
		    }
		} catch(IOException ioe) {
		    ioe.printStackTrace();
		} finally {
		    try {
				in.close();
				out.close();
				socket.close();
		    } catch (IOException ioe) {
		    	 ioe.printStackTrace();
		    } finally {
		    	synchronized(handlers) {
		    		handlers.removeElement(this);
		    	}
		    }
		}
    }
    
    private void shutdown() throws Exception {
        out.println(RESPONSES.OK.toString());
        
        for (int i = 0; i < handlers.size(); i++) {	
		    synchronized(handlers) {
				ChildThread handler = (ChildThread) handlers.elementAt(i);
			    handler.out.println("s: " + RESPONSES.SHUTDOWN_MESSAGE);
			    handler.out.println("exit");
			    handler.out.flush();
		    }
		}
        
        out.close();
        in.close();
        socket.close();
        System.exit(0);
    }
    
    public User getUser() {
    	return user;
    }
    
    public static void log(String message) {
        System.out.println(message);
    }
}

