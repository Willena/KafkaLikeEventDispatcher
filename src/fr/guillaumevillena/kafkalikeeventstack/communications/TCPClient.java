package fr.guillaumevillena.kafkalikeeventstack.communications;

import java.net.*;
import java.io.*;
import java.util.*;


//The TCPClient that can be run as a console
public class TCPClient implements ClientMessageListener {

    // notification

    // for I/O
    private ObjectInputStream sInput;        // to read from the socket
    private ObjectOutputStream sOutput;        // to write on the socket
    private Socket socket;                    // socket object
    private String server;    // server and username
    private int port; //port

    private ArrayList<ClientMessageListener> clientMessageListeners = new ArrayList<>();


    public TCPClient(String server, int port) {
        this.server = server;
        this.port = port;
    }

    /*
     * To start the chat
     */
    public boolean start() {
        // try to connect to the server
        try {
            socket = new Socket(server, port);
        }
        // exception handler if it failed
        catch (Exception ec) {
            display("Error connectiong to server:" + ec);
            return false;
        }

        String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
        display(msg);

        /* Creating both Data Stream */
        try {
            sInput = new ObjectInputStream(socket.getInputStream());
            sOutput = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException eIO) {
            display("Exception creating new Input/output Streams: " + eIO);
            return false;
        }

        // creates the Thread to listen from the server
        new ListenFromServer(this).start();
        // Send our username to the server this is the only message that we
        // will send as a String. All other messages will be ChatMessage
        // success we inform the caller that it worked
        return true;
    }

    /*
     * To send a message to the console
     */
    private void display(String msg) {
        System.out.println(msg);
    }

    /*
     * To send a message to the server
     */
    public void sendMessage(Object msg) {
        try {
            sOutput.writeObject(msg);
        } catch (IOException e) {
            display("Exception writing to server: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }

    /*
     * When something goes wrong
     * Close the Input/Output streams and disconnect
     */
    public void disconnect() {
        try {
            if (sInput != null) sInput.close();
        } catch (Exception e) {
        }
        try {
            if (sOutput != null) sOutput.close();
        } catch (Exception e) {
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
        }

    }


    public void addMessageListener(ClientMessageListener listener) {
        clientMessageListeners.add(listener);
    }


    @Override
    public void onMessageReceived(ClientSocketThread clientSocketThread, Object msg) {
        for (ClientMessageListener l : clientMessageListeners)
            l.onMessageReceived(null, msg);
    }

    /*
     * a class that waits for the message from the server
     */
    class ListenFromServer extends Thread {

        private final TCPClient client;

        public ListenFromServer(TCPClient tcpClient) {
            this.client = tcpClient;
        }

        public void run() {
            while (true) {
                try {
                    Object object = sInput.readObject();
                    client.onMessageReceived(null, object);
                } catch (IOException e) {
                    display("TCPServer has closed the connection: " + e);
                    e.printStackTrace();
                    break;
                } catch (ClassNotFoundException e2) {
                }
            }
        }
    }


    public static void main(String[] args) {
        // default values if not entered
        int portNumber = 1500;
        String serverAddress = "localhost";
        String userName = "Anonymous";

        Scanner scan = new Scanner(System.in);

        // create the TCPClient object
        TCPClient client = new TCPClient(serverAddress, portNumber);
        // try to connect to the server and return if not connected
        if (!client.start())
            return;

        System.out.println("\nHello.! Welcome to the chatroom.");

        // infinite loop to get the input from the user
        while (true) {
            System.out.print("> ");
            String msg = scan.nextLine();
            if (msg.equals("quit"))
                break;
            client.sendMessage(msg);

        }
        // close resource
        scan.close();
        client.disconnect();
    }

}

