package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/** Created by erik.huizinga on 2-2-17. */
public class Server {

  public static final String USAGE = "usage: java " + Server.class.getName() + " <name> <port>";

  private final String name;
  private final ServerSocket serverSocket;

  // TODO Map<Socket, Peer>
  private final Socket socket;
  private final Peer peer;

  public Server(String name, int port) {
    this.name = name;
    ServerSocket serverSocket = null;
    Socket socket = null;
    try {
      serverSocket = new ServerSocket(port);
      socket = serverSocket.accept();
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.serverSocket = serverSocket;
    this.socket = socket;
    peer = new Peer(name, socket);
  }

  public static void main(String[] args) {
    if (args.length != 2) {
      System.out.println("incorrect number of input arguments");
      System.out.println(USAGE);
      System.exit(0);
    }

    String name = args[0];
    if (name.equals("") || name.equals(null)) {
      System.out.println("first argument must not be null or empty");
      System.out.println(USAGE);
      System.exit(0);
    }

    int port = Integer.parseInt(args[1]);
    if (name.equals("") || name.equals(null)) {
      System.out.println("second argument must not be null or empty");
      System.out.println(USAGE);
      System.exit(0);
    }

    Server server = new Server(name, port);
    server.startServer();
    server.shutDown();
  }

  private void startServer() {
    peer.startPeer();
  }

  private void shutDown() {
    try {
      socket.close();
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
