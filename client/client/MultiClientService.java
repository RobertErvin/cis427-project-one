/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class MultiClientService {
    public static Socket channel = null;
    
    // Open the socket and initialize the listener and reader
    public static void main(String args[]) throws IOException {
        try {
            channel = new Socket ("127.0.0.1", 9898);
            
            Thread thread1 = new Thread(new Clistener(channel));
            thread1.start();
            
            Thread thread2 = new Thread(new Cconsole(channel));
            thread2.start();
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            System.exit(1);
        }
    }
}

// Handle listening to the socket output
class Clistener implements Runnable {
    Socket socket;
    public static BufferedReader br = null;
    public static Boolean confirm = false;
    public static String output; 
    
    public Clistener(Socket socket) throws IOException {
        this.socket = socket;
        br = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
    }
    
    public void run () {
        try {
            System.out.println("Client started. Waiting for input.");
            
            while (true) {
                output = br.readLine();
                
                if (output != null) {
                    System.out.println("s: " + output);
                    
                    if (output.equals("200 OK") && Cconsole.stageQuit == true 
                    		|| output.contains("210 the server")) {
                    	System.out.println("Exiting process...");
                        confirm = true;
                        br.close();
                        System.exit(0);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

// Handle writing to the socket input
class Cconsole implements Runnable {
    Socket socket = null;
    public static boolean stageQuit = false;
    public static PrintWriter out = null;
    public static BufferedReader in = null;
    public static String inputL = null;
    
    public Cconsole(Socket socket) throws IOException {
        this.socket = socket;
        out = new PrintWriter(this.socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public void run () {
        try {
        	String input = null;
            
            while (!Clistener.confirm) {   
                input = in.readLine();
                
                stageQuit = input.equals("QUIT");
                
                if (input != null) {
                    out.println(input);
                }
            }
            
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}
