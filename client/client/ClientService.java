package client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Scanner;

public class ClientService {

    private static boolean quit = false;
    private static SocketClient socket;
    private static String response;
    
    public static void main(String[] args) throws IOException {
        // TODO code application logic here
        if(args.length == 0){
            System.out.println("Please input ip address as an argument");
            System.exit(0);
        }
        try {
            socket = new SocketClient(args[0]);

            while (!quit){
                displayOptions();
                runCommands();
            }
        } catch (ConnectException e){
            System.out.println(e.getMessage());
            System.out.println("Please run the server first or input the correct ip_address");
            System.out.println("How to run: make ADDRESS=\"<address>\" ");
        } catch (Exception e){
            e.printStackTrace();
        }
    }
    
    public static void displayOptions(){
        System.out.println("Please choose one of the options:");
        System.out.println("1. MSGGET");
        System.out.println("2. MSGSTORE");
        System.out.println("3. LOGIN");
        System.out.println("4. LOGOUT");
        System.out.println("5. SHUTDOWN");
        System.out.println("6. QUIT");
    }
    
    public static void disconnect(){
        if(response.equals("200 OK")){
            quit = true;
        }
    }
    
    public static void runCommands() throws IOException{
        System.out.print("Please enter option number: ");
        Scanner input = new Scanner(System.in);
        switch(input.nextInt()){
            //MSGGET 
            case 1:
                response = socket.MSGGET();
                break;
            case 2:
                response = socket.MSGSTORE();
                break; 
            case 3:
                response = socket.LOGIN();
                break;
            case 4:
                response = socket.LOGOUT();
                break;
            case 5:
                response = socket.SHUTDOWN();
                disconnect();
                break;
            case 6:
                response = socket.QUIT();
                disconnect();
                break;
            default:
                System.out.println("Please enter a number 1-6");
        }
    }
    
}
