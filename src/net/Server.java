package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;

/** Created by erik.huizinga on 2-2-17. */
public class Server {

  public static final String USAGE = "usage: java " + Server.class.getName() + " <name> <port>";

  /** The name. */
  private final String name;

  /** The {@code ServerSocket}. */
  private final ServerSocket serverSocket;

  /** The list of peers to the clients */
  private final ClientList clients = new ClientList();

  /** The handler of peers to the clients. */
  private final PeerHandler peerHandler = new PeerHandler();

  /**
   * The switch indicating whether or not the {@code Server} is open to accept new connections from
   * clients.
   */
  private boolean isOpen;

  public Server(String name, int port) {
    this.name = name;
    ServerSocket serverSocket = null;
    try {
      serverSocket = new ServerSocket(port);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.serverSocket = serverSocket;
    clients.addObserver(peerHandler);
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
    server.stopServer();
  }

  private void acceptClients() {
    Socket socket;
    Peer peer;
    while (isOpen) {
      try {
        socket = serverSocket.accept();
        peer = new Peer(socket);
        clients.add(peer);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  private void startServer() {
    isOpen = true;
    acceptClients();
  }

  private void stopServer() {
    isOpen = false;
    try {
      serverSocket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private class ClientList extends Observable {
    private final Collection<Peer> clients = new HashSet<>();

    public Peer add(Peer peer) {
      clients.add(peer);
      setChanged();
      notifyObservers(peer);
      return peer;
    }
  }

  private class PeerHandler implements Observer {

    @Override
    public void update(Observable observable, Object arg) {
      if (observable instanceof ClientList && arg instanceof Peer) {
        Peer peer = (Peer) arg;
        new ClientHandler(peer).startClientHandler();
      }
    }
  }

  private class ClientHandler extends Observable implements Runnable {

    private final Peer peer;

    public ClientHandler(Peer peer) {
      this.peer = peer;
      addObserver(peer);
    }

    @Override
    public void run() {
      peer.startPeer();
    }

    public void startClientHandler() {
      new Thread(this).start();
    }
  }
}
