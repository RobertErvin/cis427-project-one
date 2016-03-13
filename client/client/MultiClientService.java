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
/**
 *
 * @author Nazariy
 */
public class MultiClientService {
    public static Socket channel= null;
    
    public static void main(String args[]) throws IOException {
        try {
            channel= new Socket ("127.0.0.1", 9898);
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

class Clistener implements Runnable {
    Socket socket=null;
//    public static PrintWriter out = null;
    public static BufferedReader br = null;
    public static Boolean confirm = false;
    public static String output; 
    
    public Clistener() {}
    
    public Clistener(Socket Soc) throws IOException {
        this.socket= Soc;
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }
    
    public void run () {
        try {
            System.out.println("Client started. Waiting for input.");
            
            while (true) {
                output = br.readLine();
                
                if (output != null) {
                    System.out.println("S:" + output);
                    
                    if (Cconsole.quit && output.contains("200 OK")){
                        confirm = true;
                        br.close();
                        break;
                    } else if (Cconsole.quit == true && !output.contains("200 OK")) {
                        Cconsole.quit = false;
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
}

class Cconsole implements Runnable {
    Socket socket = null;
    public static Boolean quit = false;
    public static PrintWriter out = null;
    public static BufferedReader in = null;
    public static String inputL = null;
    
    public Cconsole(Socket Soc) throws IOException {
        this.socket=Soc;
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(System.in));
    }
    
    public void run () {
        try {
        	System.out.println("Running client application");
        	
            String input = null;
            
            while (!quit && !Clistener.confirm) {   
                input = in.readLine();
                
                if (input != null) {
                    out.println(input);
                    String response = readResponse();
                    
                    if (input.equals("QUIT") && response.equals("200 OK")) {
                    	quit = true;
                    } else if (response.equals("210 the server is about to shutdown……")) {
                    	quit = true;
                    }
                }
            }
            
            out.close();
            in.close();
            socket.close();
        } catch (Exception e) {
            System.out.println("Exception: " + e.getMessage());
        }
    }
    
    public static String readResponse() throws IOException{
        String response = "";
        String output = "";
        String line;
        while(!(line = in.readLine()).equals("exit") && line != null){
            output += "s: " + line + "\n";
            response += line;
        } 
         
        System.out.println(output);
                 
        return response;
         
    } 
}


