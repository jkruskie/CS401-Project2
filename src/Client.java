// The client class will implement the functions listed in the project description.
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.lang.*; 

public class Client {

    static int serverPort;
    static InetAddress ip = null;
    static int peerID;
    static int peer_listen_port;
    static char[] FILE_VECTOR;

    public static void main(String args[]) {

        ArrayList<String> configVals = new ArrayList<>();
        Socket s;
        ObjectOutputStream outputStream;
        ObjectInputStream inputStream;
        ClientPacketHandler cs;

        // parse client config and server ip.
        ip = parseIp(args);

        try (BufferedReader br = new BufferedReader(new FileReader(args[1]))) {
            String line;
            while ((line = br.readLine()) != null) {
                configVals.add(line.split(" ")[1]);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        peerID = Integer.parseInt(configVals.get(0));
        serverPort = Integer.parseInt(configVals.get(1));
        peer_listen_port = Integer.parseInt(configVals.get(2));
        FILE_VECTOR = ("" + configVals.get(3)).toCharArray();

        System.out.println("IP Address: " + ip);
        System.out.println("Peer ID: " + peerID);
        System.out.println("Server Port: " + serverPort);
        System.out.println("Peer Listen Port: " + peer_listen_port);
        System.out.println("File Vector : "+String.valueOf(FILE_VECTOR));

        // create client object and connect to server. If successfull, print success message , otherwise quit.

        // Once connected, send registration info, event_type=0
        // start a thread to handle server responses. This class is not provided. You can create a new class called ClientPacketHandler to process these requests.

        //done! now loop for user input
        try {
            s= new Socket(ip,serverPort);
            inputStream = new ObjectInputStream(s.getInputStream());
            outputStream = new ObjectOutputStream(s.getOutputStream());

            cs = new ClientPacketHandler(s,outputStream);
            Scanner input = new Scanner(System.in);

            System.out.println("Connected to server ... " );
            cs.start();

            // Report to the server
            Packet p = new Packet();
            p.peerID = peerID;
            p.port_number = serverPort;
            p.peer_listen_port = peer_listen_port;
            p.FILE_VECTOR = FILE_VECTOR;
            p.event_type = 0;

            outputStream.writeObject(p);

            // Ask user to query
            System.out.println("Enter query");

            while (true){
                p = (Packet) inputStream.readObject();
                int event_type = p.event_type;

                switch (event_type) {
                    case (2):
                        if(p.peerID == peerID) {
                            // Return self, so we have it already
                            System.out.println("You already have this file block");
                        } else if(p.peerID == -1) {
                            // Nobody has the file
                            System.out.println("Server says that no client has file " + p.req_file_index);
                        } else {
                            // Some peer has the file
                            System.out.println("Server says that peer " + p.peerID + " on listening port " +
                                    p.peer_listen_port + " has file " + p.req_file_index);
                        }
                        break;
                    case (6):
                        System.out.println("Quiting...");
                        s.close();
                }
            }

        } catch (Exception e) {
            // TODO Auto-generated catch block
            //e.printStackTrace();
            System.out.println("Connection closed by Server..Exiting");
            System.exit(0);
        }

    }


    // implement other methods as necessary

    // Go through client config and assign variables
    public static InetAddress parseIp(String args[]) {
//            System.out.println(args[0]); // Outputs given arguments
        try {
             return InetAddress.getByName(args[0]); // Assuming 0 is hostname/ip
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }



    static class ClientPacketHandler extends Thread {
        Socket s;
        ObjectOutputStream ostream;

        public ClientPacketHandler(Socket s, ObjectOutputStream ostream) {
            this.s = s;
            this.ostream = ostream;
        }

        public void run() {
            Scanner input = new Scanner(System.in);
            try {
                Packet p = new Packet();
                while (true) {
                    String msg = input.nextLine();
                    switch(msg) {
                        case "whoami":
                            // Ask server who I am
                            // Just for testing purposes
                            p = new Packet();
                            p.event_type = 123;
                            ostream.writeObject(p);
                            break;
                        case "q":
                            // Report the quit to the server
                            p = new Packet();
                            p.event_type = 5;
                            ostream.writeObject(p);
                            break;
                        case "f":
                            // Report the file request to the server
                            System.out.println("Enter the file index you want.");
                            int index = input.nextInt();
                            p = new Packet();
                            p.req_file_index = index;
                            p.event_type = 1;
                            ostream.writeObject(p);
                            break;
                        case "p":
                            // Request a list of active clients
                            p = new Packet();
                            p.event_type = 3; // I'm going to use event type 3 for this because it isn't used for this project
                            ostream.writeObject(p);
                            break;
                    }
                }
            } catch (Exception e) {
                System.out.println("Thread interrupted...");
            }
        }

    }


}
