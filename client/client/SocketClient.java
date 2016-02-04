//Purpose: Create a connection to a server and communication with the server
package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class SocketClient {
    public static final int SERVER_PORT = 9898;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader br;
    private static String endpoint;
    private static String response;
    private static Scanner input;

    //Initialize all the variables
    SocketClient(String ip_address) throws IOException{
        //start a connection
        socket = new Socket(ip_address, SERVER_PORT);
        //stream to write to server
        out = new PrintWriter(socket.getOutputStream(), true);
        //stream to get output from server
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        input = new Scanner(System.in);
    }
    //close all streams and connections with server
    public static void disconnect() throws IOException{
        out.close();
        br.close();
        socket.close();
    }
    //call MSGGET to server
    public static String MSGGET() throws IOException{
        endpoint = "MSGGET";
        System.out.println("c: "+endpoint);
        out.println(endpoint);
        response = readResponse();
        return response;
    }
    
    //call QUIT to server
    public static String QUIT() throws IOException{
        endpoint = "QUIT";
        System.out.println("c: " + endpoint);
        out.println(endpoint);
        response = readResponse();
        if (response.equals("200 OK")){
            disconnect();
        }
        return response;
    }
    //call LOGIN to server
    public static String LOGIN() throws IOException{
        endpoint = "LOGIN";
        System.out.print("c: " + endpoint + " ");
        String cridentials = input.nextLine();
        out.println(endpoint + " " + cridentials);
        response = readResponse();
        return response;
    }
    //call LOGOUT to the server
    public static String LOGOUT() throws IOException{
        endpoint = "LOGOUT";
        System.out.println("c: " + endpoint);
        out.println(endpoint);
        response = readResponse();
        return response;
    }
    //call MSGSTORE to the server
    public static String MSGSTORE() throws IOException{
        endpoint = "MSGSTORE";
        System.out.println("c: " + endpoint);
        out.println(endpoint);
        response = readResponse();
        if (response.equals("200 OK")){
            //If response is 200 OK ask for the messege input 
            System.out.print("c: ");
            //messege to send to the server
            String messege = input.nextLine();
            out.println(messege);
            response = readResponse();
        }
            
        return response;
    }
    //call shutdown to server
    public static String SHUTDOWN() throws IOException{
        endpoint = "SHUTDOWN";
        System.out.println("c: " + endpoint);
        out.println(endpoint);
        response = readResponse();
        if (response.equals("200 OK")){
            //if response is OK close connection and exit
            disconnect();
        }
        return response;
    }
    
    public static String readResponse() throws IOException{
        String response = "";
        do{
            //read response form server
            String messege = br.readLine();
            response+=messege;
            //print response from server line by line
            System.out.println("s: "+messege);
        }while(br.ready());
        
        return response;
        
    }
}
