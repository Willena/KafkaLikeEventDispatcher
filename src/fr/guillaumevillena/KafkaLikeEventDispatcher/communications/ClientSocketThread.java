package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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

  public void setClientDisconnectedListener(ClientDisconnectedListener clientDisconnectedListener) {
    this.clientDisconnectedListener = clientDisconnectedListener;
  }

  public void setErrorEncounteredListener(ErrorEncounteredListener errorEncounteredListener) {
    this.errorEncounteredListener = errorEncounteredListener;
  }

  public void setClientMessageListener(ClientMessageListener clientMessageListener) {
    this.clientMessageListener = clientMessageListener;
  }

  private void callClientDisconenctedListener() {
    if (clientDisconnectedListener != null)
      clientDisconnectedListener.onClientDisconnected(this);
  }

  private void callClientConnectedListenner() {
    if (clientConnectedListener != null)
      clientConnectedListener.onClientConnected(this);
  }

  private void callClientMessageListener(Object msg) {
    if (clientMessageListener != null)
      clientMessageListener.onMessageReceived(this, msg);
  }

  private void callErrorListenner(String str, Exception e) {
    if (errorEncounteredListener != null)
      errorEncounteredListener.onError(this, str, e);
  }

  public void setClientConnectedListener(ClientConnectedListener clientConnectedListener) {
    this.clientConnectedListener = clientConnectedListener;
  }

  // infinite loop to read and forward message
  public void run() {
    // to loop until LOGOUT
    boolean keepGoing = true;
    while (keepGoing) {
      // read a String (which is an object)
      try {
        cm = sInput.readObject();
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

  // close everything
  private void close() {
    try {
      if (sOutput != null) sOutput.close();
      if (sInput != null) sInput.close();
      if (socket != null) socket.close();
    } catch (Exception e) {
    }

    callClientDisconenctedListener();
  }

  public boolean isConnectionAlive() {
    if (!socket.isConnected()) {
      close();
      return false;
    }
    return true;
  }

  public boolean send(Object msg) {
    if (!isConnectionAlive())
      return false;

    try {
      sOutput.writeObject(msg);
    } catch (IOException e) {
      callErrorListenner("Error sending message to ", e);
    }
    return true;
  }

}
