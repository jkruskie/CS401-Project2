// The connection Thread is spawned from the ServerSocketHandler class for every new Client connections. Responsibilities for this thread are to hnadle client specific actions like requesting file, registering to server, and client wants to quit.
import java.io.*;
import java.net.*;
import java.util.*;

class Connection extends Thread
{
    Socket socket;
    ObjectInputStream inputStream;
    ObjectOutputStream outputStream;
    int peerPort;
    int peer_listen_port;
    int peerID;
    InetAddress peerIP;
    char FILE_VECTOR[];
    ArrayList<Connection> connectionList;    
    

    public Connection(Socket socket, ArrayList<Connection> connectionList) throws IOException
    {
        this.connectionList=connectionList;
        this.socket=socket;
        this.outputStream=new ObjectOutputStream(socket.getOutputStream());
        this.inputStream=new ObjectInputStream(socket.getInputStream());
        this.peerIP=socket.getInetAddress();
        this.peerPort=socket.getPort();
    }

    @Override
    public void run() {
        //wait for register packet.
        // once received, listen for packets with client requests.
        Packet p;
        while (true){
            try { 
                p = (Packet) inputStream.readObject();
                eventHandler(p);
            }
            catch (Exception e) {break;}

        }

    }


    public void eventHandler(Packet p) throws IOException {
        int event_type = p.event_type;

        switch (event_type)
        {
            case 0: //client register
                this.peerID = p.peerID;
                this.peer_listen_port = p.peer_listen_port;
                this.FILE_VECTOR = p.FILE_VECTOR;
                printAllClientInfo();
                break;
            case 1: // client is requesting a file
                System.out.println("User " + this.peerID + " is requesting file " + p.req_file_index);
                if(determineIfUserHasFile(p.req_file_index)) {
                    System.out.println("Packet Sent");
                    Packet np = new Packet();
                    np.event_type = 2;
                    np.peerID = this.peerID;
                    np.peer_listen_port = this.peer_listen_port;
                    np.req_file_index = p.req_file_index;
                    outputStream.writeObject(np);
                } else {
                    System.out.println("DOESN'T HAVE FILE");
                    Integer user = determinePacketLocation(p.req_file_index);
                    if(user != null) {
                        // User has the block
                        Packet np = new Packet();
                        np.event_type = 2;
                        np.peerID = connectionList.get(user).peerID;
                        np.peer_listen_port = connectionList.get(user).peer_listen_port;
                        np.req_file_index = p.req_file_index;
                        outputStream.writeObject(np);
                    } else {
                        // Nobody has the block
                        // -1 to signify nobody
                        Packet np = new Packet();
                        np.peerID = -1;
                        np.event_type = 2;
                        np.req_file_index = p.req_file_index;
                        outputStream.writeObject(np);
                    }
                    System.out.println(user);
                }
                break;
            case 3: // Print list of all active clients
                printAllClientInfo();
                break;
            case 5: // client wants to quit
                System.out.println("Peer ID: " + this.peerID + " at " + this.peerIP.toString() + " wants to quit.");
                Packet np = new Packet();
                np.peerID = this.peerID;
                np.event_type = 6;
                outputStream.writeObject(np); // Send Packet to have Client close connection
                connectionList.remove(this); // Remove connection
                printAllClientInfo(); // Print all client info now that someone left
                break;
            case 123: // WHOAMI
                System.out.println("WHO AM I Request:");
                System.out.println("Peer ID : "+this.peerID);
                System.out.println("Peer Listen Port : "+this.peer_listen_port);
                System.out.println("File Vector : "+String.valueOf(this.FILE_VECTOR));
                break;
        };
    }

    private Integer determinePacketLocation(int req_file_index) {
        for(int i = 0; i < connectionList.size(); i++) {
            int file = connectionList.get(i).FILE_VECTOR[req_file_index]; // Get char from array at index
            if(file == '1') {
                return i;
            }
        }
        return null;
    }

    private boolean determineIfUserHasFile(int req_file_index) {
        int file = this.FILE_VECTOR[req_file_index]; // Get char from array at index
        System.out.println("Client " + this.peerID + " is requesting file " + req_file_index);
        if(file == '1') {
            return true; // Has file
        }
        return false; // Don't have the file
    }


    private void printAllClientInfo() {
       for(int i = 0; i < connectionList.size(); i++) {
//            System.out.println(connectionList.toString());
            System.out.println("Peer ID: " + connectionList.get(i).peerID);
            System.out.println("FILE_VECTOR: " + String.valueOf(connectionList.get(i).FILE_VECTOR));
            System.out.println("-------------");
        }
    }

    //other methods go here

    @Override
    public String toString() {
        return "Connection{" +
                "socket=" + socket +
                ", peerPort=" + peerPort +
                ", peer_listen_port=" + peer_listen_port +
                ", peerID=" + peerID +
                ", peerIP=" + peerIP +
                '}';
    }
}
