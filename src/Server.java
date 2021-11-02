// The server class will implement the functions listed in the project description. 

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Server {
    
    int serverPort;
    int MAX_CONNECTED_CLIENTS;
    ServerSocket listener;
    int numClients;
    static ArrayList<Connection> connectionList;

    
    public Server(){
        serverPort=5000;
        MAX_CONNECTED_CLIENTS=20;
        listener=null;
        numClients=0;
        connectionList= new ArrayList<Connection>();
    }
    
    public static void main(String args[])
    {
        ServerSocket ss;
        Socket s = null;
        ArrayList<Connection> cList = new ArrayList<>();

        System.out.println("Running server");

        try {
            //First, let's start our server and bind it to a port(5000).
            ss = new ServerSocket(5000);

            //Next let's start a thread that will handle incoming connections
            while (true){
                s = ss.accept();
                Connection c = new Connection(s, cList);
                c.start();
                cList.add(c);
                System.out.println(s.toString());
                System.out.println("Client is connecting..." + s.toString());
                System.out.println("IP: " + s.getInetAddress());
                System.out.println("Port: " + s.getPort());
                System.out.println("Client connected. Total clients: " + cList.size());
                System.out.println("-------------");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }




        // Note in programs shown in class, at this point we listen for incoming connections in the main method. However for this project since the server has to handle incoming connections and also handle user input simultaneously, we start a separate thread to listen for incoming connections in the Server. This is the ServerSocketHandler thread, which will in turn spawn new Connection Threads, for each client connection.
        
        //Done! Now main() will just loop for user input!.
        while (true)
        {

        }
        //will quit on user input
        
    }
    // add other methods as necessaryu. For example, you will prbably need a method to print the incoming connection info.

}

