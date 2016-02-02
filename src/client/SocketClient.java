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
import java.util.Scanner;

public class SocketClient {
    public static final int SERVER_PORT = 9898;
    private static Socket socket;
    private static PrintWriter out;
    private static BufferedReader br;
    private static String endpoint;
    private static String response;
    private static Scanner input;

    
    SocketClient(String ip_address) throws IOException{
        socket = new Socket(ip_address, SERVER_PORT);
        out = new PrintWriter(socket.getOutputStream(), true);
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        input = new Scanner(System.in);
    }
    
    public static void disconnect() throws IOException{
        out.close();
        br.close();
        socket.close();
    }
    
    public static String MSGGET() throws IOException{
        endpoint = "MSGGET";
        System.out.println("c: "+endpoint);
        out.println(endpoint);
        response = readResponse();
        return response;
    }
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
    
    public static String LOGIN() throws IOException{
        endpoint = "LOGIN";
        System.out.print("c: " + endpoint + " ");
        String cridentials = input.nextLine();
        out.println(endpoint + " " + cridentials);
        response = readResponse();
        return response;
    }
    
    public static String LOGOUT() throws IOException{
        endpoint = "LOGOUT";
        System.out.println("c: " + endpoint);
        out.println(endpoint);
        response = readResponse();
        return response;
    }
    
    public static String MSGSTORE() throws IOException{
        endpoint = "MSGSTORE";
        System.out.println("c: " + endpoint);
        out.println(endpoint);
        response = readResponse();
        if (response.equals("200 OK")){
            System.out.print("c: ");
            String messege = input.nextLine();
            out.println(messege);
            response = readResponse();
        }
            
        return response;
    }
    
    public static String SHUTDOWN() throws IOException{
        endpoint = "SHUTDOWN";
        System.out.println("c: " + endpoint);
        out.println(endpoint);
        response = readResponse();
        if (response.equals("200 OK")){
            disconnect();
        }
        return response;
    }
    
    public static String readResponse() throws IOException{
        String response = "";
        do{
            String messege = br.readLine();
            response+=messege;
            System.out.println("s: "+messege);
        }while(br.ready());
        
        return response;
        
    }
}
