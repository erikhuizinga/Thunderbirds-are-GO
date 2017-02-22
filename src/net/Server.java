package net;

import static net.Protocol.Keyword.CANCEL;
import static net.Protocol.Keyword.CHAT;
import static net.Protocol.Keyword.GO;
import static net.Protocol.Keyword.PLAYER;
import static net.Protocol.Keyword.WAITING;
import static net.Protocol.SPACE;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import net.Protocol.Keyword;
import net.Protocol.MalformedArgumentsException;
import net.Protocol.UnexpectedKeywordException;

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

  /** The list of matched peers playing games. */
  private final List<List<Peer>> gameList = new LinkedList<>();

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
    checkWaitingPeerDimensionMap4DimensionMatch();
  }

  private synchronized void checkWaitingPeerDimensionMap4DimensionMatch() {
    if (waitingPeerDimensionMap.size() >= 2) {
      Map<Integer, Peer> waitingDimensionPeerMap = new HashMap<>();
      Map<Peer, Integer> waitingPeerDimensionMapCopy = new HashMap<>(waitingPeerDimensionMap);

      for (Entry<Peer, Integer> entry : waitingPeerDimensionMapCopy.entrySet()) {
        if (waitingDimensionPeerMap.containsKey(
            entry.getValue())) { // There is a match, match up the peers and handle their game
          Peer peer1 = waitingDimensionPeerMap.get(entry.getValue());
          Peer peer2 = entry.getKey();
          List<Peer> peerList = Arrays.asList(peer1, peer2);
          gameList.add(peerList);

          // Remove the peers from the waiting lists
          waitingPeerDimensionMap.remove(entry.getKey());
          waitingPeerDimensionMap.remove(waitingDimensionPeerMap.get(entry.getValue()));
          waitingDimensionPeerMap.remove(entry.getValue());

          // Start a new game
          new GameHandler(peerList).start();

          // Break the loop; we know there cannot be any more matches until a new client connects
          break;

        } else { // Store the unmatched dimension as a key to the unmatched peer
          waitingDimensionPeerMap.put(entry.getValue(), entry.getKey());
        }
      }
    }
  }

  private class ClientHandler implements Runnable {

    private final Peer peer;
    private Scanner in;
    private Thread thread;
    private boolean keepRunning = true;

    public ClientHandler(Peer peer) {
      this.peer = peer;
      in = peer.getScanner();
    }

    private Keyword expect(Keyword... keywords)
        throws UnexpectedKeywordException, MalformedArgumentsException {
      return Protocol.expect(in, keywords);
    }

    private void send(Keyword keyword, String... arguments) throws MalformedArgumentsException {
      send(Protocol.validateAndFormatCommandString(keyword, arguments));
    }

    private void send(String command) {
      peer.send(command);
    }

    private void stopClientHandler() {
      peer.shutDown();
      keepRunning = false;
    }

    @Override
    public void run() {
      do {
        Keyword keyword;
        // Client: PLAYER name
        try {
          CHAT.setExecutable(this::chatHandler);
          PLAYER.setExecutable((list) -> System.out.println("PLAYER was received"));
          keyword = expect(PLAYER, CHAT);

        } catch (UnexpectedKeywordException | MalformedArgumentsException e) {
          //TODO send a warning / shutdown peer
        }

        // Client: GO dimension
        int dimension = 0;
        try {
          keyword = expect(GO, CANCEL);
          System.out.println(keyword);

        } catch (UnexpectedKeywordException | MalformedArgumentsException e) {
          //TODO send a warning / shutdown peer
        }

        // Server: WAITING
        try {
          send(WAITING);
        } catch (MalformedArgumentsException e) {
          e.printStackTrace();
          stopClientHandler();
          return;
        }
        add2WaitingMap(peer, dimension);

      } while (keepRunning);
    }

    public void start() {
      thread = new Thread(this);
      thread.start();
    }

    void chatHandler(List<String> message) {
      for (String word : message) {
        System.out.print(word + SPACE);
      }
      System.out.println();
    }
  }

  private class GameHandler implements Runnable {

    private final List<Peer> peers;

    public GameHandler(List<Peer> peers) {
      this.peers = peers;
    }

    @Override
    public void run() {}

    public void start() {
      new Thread(this).start();
    }
  }
}
