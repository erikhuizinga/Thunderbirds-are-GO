package net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Scanner;
import net.Protocol.ClientCommand;

/** Created by erik.huizinga on 2-2-17. */
public class Server {

  public static final String USAGE = "usage: java " + Server.class.getName() + " <name> <port>";

  /** The name. */
  private final String name;

  /** The {@code ServerSocket}. */
  private final ServerSocket serverSocket;

  /** The list of client peers. */
  private final Collection<Peer> clients = new LinkedList<>();

  /** The map of peers and their desired board dimensions. */
  private final Map<Peer, Integer> waitingPeerDimensionMap = new HashMap<>();

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
        new ClientHandler(peer).start();

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

  private synchronized void add2WaitingMap(Peer peer, int dimension) {
    waitingPeerDimensionMap.put(peer, dimension);
    checkWaitingMap();
  }

  private synchronized void checkWaitingMap() {
    if (waitingPeerDimensionMap.size() > 2) {}
  }

  private class ClientHandler extends Observable implements Runnable {

    private final Peer peer;
    private Scanner in;

    public ClientHandler(Peer peer) {
      this.peer = peer;
      addObserver(peer);
    }

    @Override
    public void run() {
      in = peer.getIn();

      // PLAYER name
      List<String> argList = expect(ClientCommand.PLAYER);
      String name = argList.get(0);

      // GO dimension
      List<String> args = expect(ClientCommand.GO);
      int dimension = Integer.parseInt(args.get(0));

      add2WaitingMap(peer, dimension);
    }

    private List<String> expect(ClientCommand clientCommand) {
      List<String> argList = null;
      if (in.hasNext() && in.next().equals(clientCommand.toString())) {
        argList = Arrays.asList(in.nextLine().trim().split(Protocol.SPACE));
      }
      return (clientCommand.isValidArgList(argList)) ? argList : expect(clientCommand);
    }

    public void start() {
      new Thread(this).start();
    }
  }
}
