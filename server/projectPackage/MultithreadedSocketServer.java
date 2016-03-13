package projectPackage;

/*
 * Server.java
 */

import java.io.*;
import java.net.*;
import java.util.*;

public class MultithreadedSocketServer {

    public static final int SERVER_PORT = 5432;
    
    @SuppressWarnings("resource")
	public static void main(String args[]) {
		ServerSocket serverSocket = null;
		Socket serviceSocket = null;
	
		// Try to open a server socket 
		try {
			serverSocket = new ServerSocket(SERVER_PORT);
		} catch (IOException e) {
		    System.out.println(e);
		}
		
		// Create a socket object from the ServerSocket to listen and accept connections.
		while (true) {
		    try {
				// Received a connection
				serviceSocket = serverSocket.accept();
				System.out.println("MultiThreadServer: new connection from " + serviceSocket.getInetAddress());
		
				// Create and start the client handler thread
				ChildThread cThread = new ChildThread(serviceSocket);
				cThread.start();
		    } catch (IOException e) {
		    	System.out.println(e);
		    }
		}
    }
}
