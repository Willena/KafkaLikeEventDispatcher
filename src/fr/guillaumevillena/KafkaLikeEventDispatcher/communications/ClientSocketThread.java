package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * The socket thread class.
 * When a new client comes to the server, a new thread is created for the client holding the socket
 * This allows the server to manage asynchronously each clients.
 */
public class ClientSocketThread extends Thread {
  Socket socket;
  ObjectInputStream sInput;
  ObjectOutputStream sOutput;
  // the Username of the TCPClient
  String username;
  // message object to recieve message and its type
  Object cm;
  // timestamp
  String date;

  ClientDisconnectedListener clientDisconnectedListener = null;
  ClientConnectedListener clientConnectedListener = null;
  ClientMessageListener clientMessageListener = null;
  ErrorEncounteredListener errorEncounteredListener = null;

  /**
   * Initialize the Socket thread class.
   *
   * @param socket the socket used by the remote client
   */
  // Constructor
  ClientSocketThread(Socket socket) {

    // a unique id
    this.socket = socket;
    //Creating both Data Stream
    System.out.println("Creating IO Streams ... ");
    try {
      sOutput = new ObjectOutputStream(socket.getOutputStream());
      sInput = new ObjectInputStream(socket.getInputStream());


    } catch (IOException e) {
      callErrorListenner("Exception creating new Input/output Streams: ", e);
      return;
    }

    callClientConnectedListenner();

  }

  /**
   * Register the disconnected callback
   *
   * @param clientDisconnectedListener the callback
   */
  public void setClientDisconnectedListener(ClientDisconnectedListener clientDisconnectedListener) {
    this.clientDisconnectedListener = clientDisconnectedListener;
  }

  /**
   * Register the error callback
   *
   * @param errorEncounteredListener the callback
   */
  public void setErrorEncounteredListener(ErrorEncounteredListener errorEncounteredListener) {
    this.errorEncounteredListener = errorEncounteredListener;
  }

  /**
   * Register the message callback
   *
   * @param clientMessageListener the callback
   */
  public void setClientMessageListener(ClientMessageListener clientMessageListener) {
    this.clientMessageListener = clientMessageListener;
  }

  /**
   * Method that call all disconnected listeners
   */
  private void callClientDisconenctedListener() {
    if (clientDisconnectedListener != null)
      clientDisconnectedListener.onClientDisconnected(this);
  }

  /**
   * Method that calls all client connected listeners
   */
  private void callClientConnectedListenner() {
    if (clientConnectedListener != null)
      clientConnectedListener.onClientConnected(this);
  }

  /**
   * Method that transmit the message received via callbacks
   *
   * @param msg the message to transmit
   */
  private void callClientMessageListener(Object msg) {
    if (clientMessageListener != null)
      clientMessageListener.onMessageReceived(this, msg);
  }

  /**
   * Notify listeners that an error occurred
   *
   * @param str the string of the error
   * @param e   the exception
   */
  private void callErrorListenner(String str, Exception e) {
    if (errorEncounteredListener != null)
      errorEncounteredListener.onError(this, str, e);
  }

  /**
   * Register the listener "onclientconnected"
   *
   * @param clientConnectedListener the callback
   */
  public void setClientConnectedListener(ClientConnectedListener clientConnectedListener) {
    this.clientConnectedListener = clientConnectedListener;
  }

  /**
   * Main loop listening for events on the socket
   */
  // infinite loop to read and forward message
  public void run() {
    // to loop until LOGOUT
    boolean keepGoing = true;
    while (keepGoing) {
      // read a String (which is an object)
      try {
        cm = sInput.readUnshared();
        callClientMessageListener(cm);

      } catch (IOException e) {

        callErrorListenner(" Exception reading Streams: ", e);
        break;
      } catch (ClassNotFoundException e2) {
        break;
      }
    }

    close();
  }

  /**
   * Close the thread and the socket used. This cause the deconnection of the client
   */
  private void close() {
    try {
      if (sOutput != null) sOutput.close();
      if (sInput != null) sInput.close();
      if (socket != null) socket.close();
    } catch (Exception e) {
    }

    callClientDisconenctedListener();
  }

  /**
   * Check the connection state
   *
   * @return True if the connection with the other end is alive
   */
  public boolean isConnectionAlive() {
    if (!socket.isConnected()) {
      close();
      return false;
    }
    return true;
  }

  /**
   * Send a message to the other end
   *
   * @param msg message to send
   * @return True if the message has been sent
   */
  public synchronized boolean send(Object msg) {
    if (!isConnectionAlive())
      return false;

    try {
      sOutput.writeUnshared(msg);
    } catch (IOException e) {
      callErrorListenner("Error sending message to ", e);
    }
    return true;
  }

}
