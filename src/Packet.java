// This class defines the Pakcet strucure. This strucutre will also be used in future projects, so there are some addiotnal attrib utes included here, which will not be used in Project 2. These are marked below. When send ing a Packet, these attributes will just contain their default values specified in the constructor.
import java.util.*;
import java.io.*;
import java.io.*;
import java.net.*;
import java.util.*;

class Packet implements Serializable{
    int sender;  // sender id of packet
    int recipient;  // receipeint id of packet, not required to set for this project
    int event_type; // see below for event type codes
    int port_number;  // for reporting listening port number, can be found from attribute of socket
    int req_file_index; //which file index is requested
    char peerIP[]; // for telling the client the peer IP, not required to set for this project
    int peerID; // will contain the id of the peer that contains the file
    int peer_listen_port; // will contain the listening port of the peer that contains the file.
    char FILE_VECTOR[]; // contains the clients file_vector

    
    //EVENT_TYPE CODES
    /*
        0-> Client sending initialization infor to server
        1-> Client is requesting file form server (wants to know which peer has it)
        2-> Server is replying to client with poeer id for file request
        3-> not required for this project
        4-> not required for this project
        5-> Client wants to quit
        6-> Server wants to quit
    */
    public Packet()
    {
        sender=-1;
        recipient=-1;
        event_type=-1;
        port_number=-1;
        req_file_index=-1;
        peerIP=new char[20];
        Arrays.fill(peerIP,'0');
        peerID=-1;
        peer_listen_port=-1;
        FILE_VECTOR=new char[64];
        Arrays.fill(FILE_VECTOR,'0');

    }

    
    void printPacket()
    {
        System.out.println("Packet Contents");
        System.out.println("---------------");
        System.out.println("Sender ID : "+sender);
        System.out.println("Receiver ID : "+recipient);
        System.out.println("Event Type : "+event_type);
        System.out.println("Port Number : "+port_number);
        System.out.println("Requested File Index : "+req_file_index);
        System.out.println("Peer IP : "+String.valueOf(peerIP));
        System.out.println("Peer ID : "+peerID);
        System.out.println("Peer Listen Port : "+peer_listen_port);
        System.out.println("File Vector : "+String.valueOf(FILE_VECTOR));
    }


}

