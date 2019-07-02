package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

/**
 * A TCP threaded class
 */
public class TCPServer implements ClientDisconnectedListener, ClientConnectedListener, ClientMessageListener {
  ServerSocket serverSocket;
  private ArrayList<ClientSocketThread> clientSocketThreads = new ArrayList<>();
  private int port;
  private boolean keepGoing;
  private ArrayList<ClientConnectedListener> clientConnectedListeners = new ArrayList<>();
  private ArrayList<ClientDisconnectedListener> clientDisconnectedListeners = new ArrayList<>();
  private ArrayList<ClientMessageListener> clientMessageListeners = new ArrayList<>();

  //constructor that receive the port to listen to for connection as parameter

  /**
   * Constructor, initialize the TCP server to listen on a port given port
   *
   * @param port the port to listen on
   */
  public TCPServer(int port) {
    this.port = port;
  }

  /**
   * Start the server thread and listen for event on the socket
   *
   * @return the thread instance
   */
  public Thread start() {
    keepGoing = true;
    //create socket server and wait for connection requests

    Thread serverThread = new Thread(new Runnable() {
      @Override
      public void run() {
        try {
          // the socket used by the server
          serverSocket = new ServerSocket(port);

          // infinite loop to wait for connections ( till server is active )
          while (keepGoing) {
            System.out.println("TCPServer waiting for fr.guillaumevillena.KafkaLikeEventDispatcher.clients on port " + port + ".");

            // accept connection if requested from client
            Socket socket = serverSocket.accept();
            // break if server stoped
            if (!keepGoing)
              break;
            // if client is connected, create its thread

            ClientSocketThread t = new ClientSocketThread(socket);
            t.setClientDisconnectedListener(TCPServer.this);
            t.setClientConnectedListener(TCPServer.this);
            t.setClientMessageListener(TCPServer.this);
            clientSocketThreads.add(t);
            t.start();

          }

          onStop();

        } catch (IOException e) {
          String msg = " Exception on new ServerSocket: " + e + "\n";
          System.out.println(msg);
        }
      }
    });

    serverThread.start();

    return serverThread;

  }

  /**
   * Method that can be used to close the TCP server. This will stop the Thread loop
   */
  protected void stop() {
    keepGoing = false;
  }

  /**
   * Method called by the server thread when it finishes. It clears all remaining client.
   */
  private void onStop() {
    try {
      serverSocket.close();
      for (int i = 0; i < clientSocketThreads.size(); ++i) {
        ClientSocketThread tc = clientSocketThreads.get(i);
        try {
          // close all data streams and socket
          tc.sInput.close();
          tc.sOutput.close();
          tc.socket.close();
        } catch (IOException ioE) {
        }
      }
    } catch (Exception e) {
      System.out.println("Exception closing the server and clients: " + e);
    }
  }


  /**
   * A broadcast method to send a message to all connected clients
   *
   * @param message object to send to all
   * @return True if the message has been sent
   */
  public synchronized boolean broadcast(Object message) {

    for (ClientSocketThread th : clientSocketThreads) {
      th.send(message);
    }

    return true;

  }

  /**
   * A method to remove a client from the list of connection client
   *
   * @param clientSocketThread the client to be removed
   */
  private synchronized void remove(ClientSocketThread clientSocketThread) {
    clientSocketThreads.remove(clientSocketThread);
  }


  /**
   * Event listener to process client disconnections
   *
   * @param thread the current socket thread
   */
  @Override
  public void onClientDisconnected(ClientSocketThread thread) {
    remove(thread);
    for (ClientDisconnectedListener l : clientDisconnectedListeners) {
      l.onClientDisconnected(thread);
    }
  }

  /**
   * Event listener to process client connexions
   *
   * @param clientSocketThread the current socket thread
   */
  @Override
  public synchronized void onClientConnected(ClientSocketThread clientSocketThread) {
    System.out.println("The Client is now ready ");
    clientSocketThread.send("Hello client, this is the server ! ");

    for (ClientConnectedListener l : clientConnectedListeners) {
      l.onClientConnected(clientSocketThread);
    }
  }

  /**
   * Event listener to forward and process a received message
   *
   * @param clientSocketThread the current socket thread
   * @param msg                the received message
   */
  @Override
  public synchronized void onMessageReceived(ClientSocketThread clientSocketThread, Object msg) {
    System.out.println(clientSocketThread + " : " + msg);

    for (ClientMessageListener l : clientMessageListeners) {
      l.onMessageReceived(clientSocketThread, msg);
    }
  }

  /**
   * Adds a new listener
   *
   * @param l the listener
   */
  public void addClientConnectedListener(ClientConnectedListener l) {
    clientConnectedListeners.add(l);
  }


  /**
   * Adds a new listener
   *
   * @param l the listener
   */
  public void addClientDisconnectedListener(ClientDisconnectedListener l) {
    clientDisconnectedListeners.add(l);
  }


  /**
   * Adds a new listener
   *
   * @param l the listener
   */
  public void addClientMessageListener(ClientMessageListener l) {
    clientMessageListeners.add(l);
  }


}

