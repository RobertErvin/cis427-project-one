//Purpose: Display client interface to perform certain funcitons

package client;

import java.io.IOException;
import java.net.ConnectException;
import java.util.Scanner;

public class ClientService {

    private static boolean quit = false;
    private static SocketClient socket;
    private static String response;
    
    public static void main(String[] args) throws IOException {
        //Getting the ip_address argument from command line
        if(args.length == 0){
            System.out.println("Please input ip address as an argument");
            System.exit(0);
        }
        try {
            //creating connecting to the server
            socket = new SocketClient(args[0]);
            
            //keep conneection live until quit is true
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
    //Display options to the user
    public static void displayOptions(){
        System.out.println("Please choose one of the options:");
        System.out.println("1. MSGGET");
        System.out.println("2. MSGSTORE");
        System.out.println("3. LOGIN");
        System.out.println("4. LOGOUT");
        System.out.println("5. SHUTDOWN");
        System.out.println("6. QUIT");
    }
    
    //Disconnect from the server
    public static void disconnect(){
        if(response.equals("200 OK")){
            quit = true;
        }
    }
    //Run commands based on the optin chosen by the user
    public static void runCommands() throws IOException{
        int choice = 9;
        Scanner input = new Scanner(System.in);
        System.out.print("Please enter option number: ");
        try{
            choice = input.nextInt();
        }catch(Exception e){
            //a wrong choice that will ask the client to enter a number again
            choice = 9;
        }
        
        switch(choice){
            //MSGGET 
            case 1:
                response = socket.MSGGET();
                break;
            //MSGSTORE
            case 2:
                response = socket.MSGSTORE();
                break; 
            //LOGIN
            case 3:
                response = socket.LOGIN();
                break;
            //LOGOUT
            case 4:
                response = socket.LOGOUT();
                break;
            //SHUTDOWN
            case 5:
                response = socket.SHUTDOWN();
                disconnect();
                break;
            //QUIT
            case 6:
                response = socket.QUIT();
                disconnect();
                break;
            default:
                System.out.println("Please enter a number 1-6");
        }
    }
    
}
