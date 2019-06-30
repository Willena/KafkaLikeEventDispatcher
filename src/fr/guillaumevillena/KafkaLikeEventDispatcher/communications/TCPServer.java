package fr.guillaumevillena.KafkaLikeEventDispatcher.communications;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

// the server that can be run as a console
public class TCPServer implements ClientDisconnectedListener, ClientConnectedListener, ClientMessageListener {
  private ArrayList<ClientSocketThread> clientSocketThreads = new ArrayList<>();
  private int port;
  private boolean keepGoing;

  private ArrayList<ClientConnectedListener> clientConnectedListeners = new ArrayList<>();
  private ArrayList<ClientDisconnectedListener> clientDisconnectedListeners = new ArrayList<>();
  private ArrayList<ClientMessageListener> clientMessageListeners = new ArrayList<>();


  ServerSocket serverSocket;

  //constructor that receive the port to listen to for connection as parameter

  public TCPServer(int port) {
    this.port = port;
  }

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

  // to stop the server
  protected void stop() {
    keepGoing = false;
  }

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


  // to broadcast a message to all fr.guillaumevillena.KafkaLikeEventDispatcher.clients
  public synchronized boolean broadcast(Object message) {

    for (ClientSocketThread th : clientSocketThreads) {
      th.send(message);
    }

    return true;

  }

  synchronized void remove(ClientSocketThread clientSocketThread) {
    clientSocketThreads.remove(clientSocketThread);
  }


  @Override
  public void onClientDisconnected(ClientSocketThread thread) {
    remove(thread);
    for (ClientDisconnectedListener l : clientDisconnectedListeners) {
      l.onClientDisconnected(thread);
    }
  }

  @Override
  public void onClientConnected(ClientSocketThread clientSocketThread) {
    System.out.println("The Client is now ready ");
    clientSocketThread.send("Hello client, this is the server ! ");

    for (ClientConnectedListener l : clientConnectedListeners) {
      l.onClientConnected(clientSocketThread);
    }
  }

  @Override
  public void onMessageReceived(ClientSocketThread clientSocketThread, Object msg) {
    System.out.println(clientSocketThread + " : " + msg);

    for (ClientMessageListener l : clientMessageListeners) {
      l.onMessageReceived(clientSocketThread, msg);
    }
  }

  public void addClientConnectedListener(ClientConnectedListener l) {
    clientConnectedListeners.add(l);
  }

  public void addClientDisconnectedListener(ClientDisconnectedListener l) {
    clientDisconnectedListeners.add(l);
  }

  public void addClientMessageListener(ClientMessageListener l) {
    clientMessageListeners.add(l);
  }

//  public static void main(String[] args) throws PlayerCountInvalid {
//    // start server on port 1500 unless a PortNumber is specified
//    int portNumber = 1500;
//    TCPServer server = new TCPServer(portNumber);
//    try {
//      server.start().join();
//    } catch (InterruptedException e) {
//      e.printStackTrace();
//    }
//  }


}

