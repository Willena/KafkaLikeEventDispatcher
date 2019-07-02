package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;


/**
 * A basic TCP client class
 */
public class TCPClient implements ClientMessageListener {

  // notification

  // for I/O
  private ObjectInputStream sInput;        // to read from the socket
  private ObjectOutputStream sOutput;        // to write on the socket
  private Socket socket;                    // socket object
  private String server;    // server and username
  private int port; //port

  private ArrayList<ClientMessageListener> clientMessageListeners = new ArrayList<>();


  /**
   * Create the client with server informations
   *
   * @param server the server hostname or ip
   * @param port   the port of the server
   */
  public TCPClient(String server, int port) {
    this.server = server;
    this.port = port;
  }


  /**
   * Start the TCP client thread
   *
   * @return true is the client is started
   */
  public boolean start() {
    try {
      socket = new Socket(server, port);
    } catch (Exception ec) {
      System.out.println("Error connectiong to server:" + ec);
      return false;
    }

    String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
    System.out.println(msg);

    try {
      sInput = new ObjectInputStream(socket.getInputStream());
      sOutput = new ObjectOutputStream(socket.getOutputStream());
    } catch (IOException eIO) {
      System.out.println("Exception creating new Input/output Streams: " + eIO);
      return false;
    }

    new ListenFromServer(this).start();

    return true;
  }


  /**
   * Send a message to the server
   *
   * @param msg the message
   */
  synchronized public void sendMessage(Object msg) {
    try {
      sOutput.writeUnshared(msg);
    } catch (IOException e) {
      System.out.println("Exception writing to server: " + e);
      e.printStackTrace();
      System.exit(1);
    }
  }


  /**
   * Disconnect from the server in a "clean" way.
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


  /**
   * Adds the listener on each message received
   *
   * @param listener the listener
   */
  public void addMessageListener(ClientMessageListener listener) {
    clientMessageListeners.add(listener);
  }


  /**
   * The callback from the socket thread to be forwarded to any initialized listeners
   *
   * @param clientSocketThread the current socket thread
   * @param msg                the received message
   */
  @Override
  public synchronized void onMessageReceived(ClientSocketThread clientSocketThread, Object msg) {
    for (ClientMessageListener l : clientMessageListeners)
      l.onMessageReceived(null, msg);
  }

  /**
   * The socket listener thread
   */
  class ListenFromServer extends Thread {

    private final TCPClient client;

    /**
     * Configure the socket listener thread
     *
     * @param tcpClient the TCP client instance
     */
    public ListenFromServer(TCPClient tcpClient) {
      this.client = tcpClient;
    }

    /**
     * Real waiting work. It wait for any message on the socket and tries to recreate the original object sent
     */
    public void run() {
      while (true) {
        try {
          Object object = sInput.readUnshared();
          client.onMessageReceived(null, object);
        } catch (IOException e) {
          System.out.println("TCPServer has closed the connection: " + e);
          e.printStackTrace();
          break;
        } catch (ClassNotFoundException e2) {
        }
      }
    }
  }

}

