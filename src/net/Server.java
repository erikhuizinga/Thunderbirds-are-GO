package net;

import static net.Protocol.BLACK;
import static net.Protocol.Keyword.CANCEL;
import static net.Protocol.Keyword.CHAT;
import static net.Protocol.Keyword.EXIT;
import static net.Protocol.Keyword.GO;
import static net.Protocol.Keyword.MOVE;
import static net.Protocol.Keyword.PLAYER;
import static net.Protocol.Keyword.READY;
import static net.Protocol.Keyword.WAITING;
import static net.Protocol.Keyword.WARNING;
import static net.Protocol.SPACE;
import static net.Protocol.WHITE;
import static net.Protocol.isValidDimension;

import game.Go;
import game.Rules;
import game.action.Move;
import game.action.Move.MoveType;
import game.material.Stone;
import game.material.board.Board;
import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Observable;
import java.util.Observer;
import java.util.stream.Collectors;
import net.Protocol.Command;
import net.Protocol.Executable;
import net.Protocol.Keyword;
import net.Protocol.MalformedArgumentsException;
import net.Protocol.UnexpectedKeywordException;
import players.Player;
import players.RemotePlayer;

/** Created by erik.huizinga on 2-2-17. */
public class Server {

  public static final String DEFAULT_ADDRESS = "localhost";

  public static final String DEFAULT_PORT = "1336";

  public static final String USAGE = "usage: java " + Server.class.getName() + " <name> <port>";

  /** The name. */
  private final String name;

  /** The {@code ServerSocket}. */
  private final ServerSocket serverSocket;

  /** The list of client peers. */
  private final List<Client> clients = Collections.synchronizedList(new LinkedList<>());

  /** The map of peers and their desired board dimensions. */
  private final Map<Client, Integer> waitingClientDimensionMap = new HashMap<>();

  /** The list of games. */
  private final List<GameHandler> games = Collections.synchronizedList(new LinkedList<>());

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
      stopServer();
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
    if (name == null || name.equals("")) {
      System.out.println("first argument must not be null or empty");
      System.out.println(USAGE);
      System.exit(0);
    }

    int port = -1;
    try {
      port = Integer.parseInt(args[1]);
    } catch (NumberFormatException e) {
      System.err.println("second argument must be a valid port number");
      System.out.println(USAGE);
      System.exit(0);
    }
    if (port < 0 || port > 65535) {
      System.err.println("second argument must be a port number between 0 and 65535, inclusive");
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
        new ClientHandler(peer).start();
        Thread.sleep(10);

      } catch (IOException | InterruptedException e) {
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
    } catch (IOException | NullPointerException e) {
      e.printStackTrace();
    }
  }

  private synchronized GameHandler handleWaitingClient(Client client, int dimension) {
    waitingClientDimensionMap.put(client, dimension);
    return matchWaitingPeers();
  }

  private synchronized GameHandler matchWaitingPeers() {
    if (waitingClientDimensionMap.size() >= 2) {
      Map<Integer, Client> waitingDimensionClientMap = new HashMap<>();
      Map<Client, Integer> waitingClientDimensionMapCopy = new HashMap<>(waitingClientDimensionMap);

      for (Entry<Client, Integer> entry : waitingClientDimensionMapCopy.entrySet()) {
        //TODO assert connected status
        if (waitingDimensionClientMap.containsKey(entry.getValue())) {
          // There is a dimension match, match the clients and handle their game
          Client client1 = waitingDimensionClientMap.get(entry.getValue());
          Client client2 = entry.getKey();
          int dimension = entry.getValue();

          // Remove the peers from the waiting lists
          waitingClientDimensionMap.remove(entry.getKey());
          waitingClientDimensionMap.remove(waitingDimensionClientMap.get(entry.getValue()));
          waitingDimensionClientMap.remove(entry.getValue());

          // Start a new game
          return new GameHandler(client1, client2, dimension);

        } else { // Store the unmatched dimension as a key to the unmatched peer
          waitingDimensionClientMap.put(entry.getValue(), entry.getKey());
        }
      }
    }
    return null;
  }

  private Command expect(
      BufferedReader reader,
      Executable chat,
      Executable cancel,
      Executable exit,
      Command... expectedCommands)
      throws UnexpectedKeywordException, MalformedArgumentsException {
    List<Command> expectedCommandList =
        Arrays.stream(expectedCommands).collect(Collectors.toList());

    Command chatCommand = new Command(CHAT);
    chatCommand.setExecutable(chat);
    expectedCommandList.add(chatCommand);

    Command cancelCommand = new Command(CANCEL);
    cancelCommand.setExecutable(cancel);
    expectedCommandList.add(cancelCommand);

    Command exitCommand = new Command(EXIT);
    exitCommand.setExecutable(exit);
    expectedCommandList.add(exitCommand);

    return Protocol.expect(reader, expectedCommandList.toArray(new Command[] {}));
  }

  private class ClientHandler implements Runnable {

    private final Peer peer;
    private BufferedReader in;
    private Thread thread;
    private boolean keepRunning = true;
    private boolean waiting4Player = true;
    private String playerName = null;

    public ClientHandler(Peer peer) {
      this.peer = peer;
      in = peer.getReader();
    }

    private Command expect(Command... expectedCommands)
        throws UnexpectedKeywordException, MalformedArgumentsException {
      return Server.this.expect(
          in,
          this::chatHandler,
          argList -> stopClientHandler(),
          argList -> stopClientHandler(),
          expectedCommands);
    }

    private void send(Keyword keyword, String... arguments) throws MalformedArgumentsException {
      peer.send(Protocol.validateAndFormatCommandString(keyword, arguments));
    }

    private void stopClientHandler() {
      peer.shutDown();
      keepRunning = false;
    }

    @Override
    public void run() {
      while (keepRunning) {
        // Client: PLAYER name
        Client client = receiveClient();
        if (!keepRunning) {
          return;
        }

        // Client: GO dimension
        int dimension = receiveDimension();
        if (!keepRunning) {
          return;
        }

        // Server: WAITING
        try {
          send(WAITING);
        } catch (MalformedArgumentsException e) {
          e.printStackTrace();
          stopClientHandler();
          return;
        }

        // Handle waiting clients
        GameHandler gameHandler = handleWaitingClient(client, dimension);

        // Start a new game
        if (gameHandler != null) {
          games.add(gameHandler);
          gameHandler.start();
        }
      }
    }

    private Client receiveClient() {
      final Client[] client = new Client[1];
      Command playerCommand = new Command(PLAYER);
      playerCommand.setExecutable(
          argList -> {
            playerName = argList.get(0);
            client[0] = new Client(playerName, peer);
            clients.add(client[0]);
            waiting4Player = false;
          });
      do {
        try {
          expect(playerCommand).printAndExecute();

        } catch (UnexpectedKeywordException e) {
          warn("unexpected keyword");
        } catch (MalformedArgumentsException e) {
          warn("malformed argument(s)");
        }
      } while (waiting4Player && keepRunning);
      return client[0];
    }

    private int receiveDimension() {
      final int[] dimension = {0};
      Command goCommand = new Command(GO);
      goCommand.setExecutable(
          argList -> {
            try {
              dimension[0] = Integer.parseInt(argList.get(0));
            } catch (NumberFormatException e) {
              warn("invalid dimension sent with " + GO + " keyword");
            }
            if (argList.size() > 1) {
              warn("specifying opponent name not supported by " + name + " the server"); //TODO
            }
          });
      do {
        try {

          expect(goCommand).printAndExecute();

        } catch (UnexpectedKeywordException e) {
          warn("unexpected keyword");
        } catch (MalformedArgumentsException e) {
          warn("malformed argument(s)");
        }
      } while (keepRunning && !isValidDimension(dimension[0]));
      return dimension[0];
    }

    private void chatHandler(List<String> argList) {
      // Log chat message to console
      argList.add(0, getPlayerName() + ":");
      Protocol.chatPrinter(argList);

      // Broadcast message to all clients //TODO only waiting clients
      String command;
      try {
        command = Protocol.validateAndFormatCommandString(CHAT, argList.toArray(new String[] {}));
      } catch (MalformedArgumentsException e) {
        warn("CHAT arguments malformed");
        return;
      }
      for (Client client : clients) {
        Peer peer = client.getPeer();
        if (peer.equals(this.peer)) {
          continue;
        }
        peer.send(command);
      }
    }

    private String getPlayerName() {
      return (playerName == null) ? "UNKNOWN" : playerName;
    }

    private void warn(String message) {
      try {
        peer.send(Protocol.validateAndFormatCommandString(WARNING, message.split(SPACE)));
      } catch (MalformedArgumentsException e) {
        System.err.println("Wachoouptoo, server?");
      }
    }

    public void start() {
      thread = new Thread(this);
      thread.start();
    }
  }

  private class GameHandler implements Runnable, Observer {

    private final int BLACK_INDEX;
    private final Client blackClient;
    private final Client whiteClient;
    private final Player blackPlayer;
    private final Player whitePlayer;
    private final int dimension;
    private final List<Client> clients;
    private final Thread thread = new Thread(this);
    private Go go;

    public GameHandler(Client client1, Client client2, int dimension) {
      clients = Arrays.asList(client1, client2);
      BLACK_INDEX = (int) Math.round(Math.random());
      blackClient = clients.get(BLACK_INDEX);
      whiteClient = clients.get(1 - BLACK_INDEX);
      blackPlayer = new RemotePlayer(Stone.BLACK, blackClient.getName());
      whitePlayer = new RemotePlayer(Stone.WHITE, whiteClient.getName());
      this.dimension = dimension;
    }

    @Override
    public void run() {
      go = new Go(dimension, blackPlayer, whitePlayer);
      go.addObserver(this);

      // Server: READY thisColour opponentName dimension
      sendReady();

      // Play GO
      Thread goThread = new Thread(go);
      goThread.start();

      // Handle moves by the players
      Command moveCommand = new Command(MOVE);
      moveCommand.setExecutable(
          argList -> {
            RemotePlayer player = (RemotePlayer) go.getCurrentPlayer();
            int x = Integer.parseInt(argList.get(0));
            int y = Integer.parseInt(argList.get(1));

            Client client;
            if (player.getStone() == Stone.BLACK) {
              client = blackClient;
            } else {
              client = whiteClient;
            }

            Board currentBoard = go.getBoard();
            Move move = new Move(x, y, player.getStone());

            if (Rules.isTechnicallyValid(currentBoard, move)) {
              Board nextBoard = Rules.playWithDynamicalValidation(currentBoard, move);
              if (Rules.isHistoricallyValid(go, nextBoard)) {
                sendValid(client, move);
              } else {
                sendInvalid(client);
              }
            } else {
              sendInvalid(client);
            }
          });

      while (!Rules.isFinished(go)) {
        //TODO expect MOVE, PASS, TABLEFLIP
      }
    }

    private void sendInvalid(Client client) {
      // send(...) // kick invalid client, notify the other
    }

    private void sendValid(Client client, Move move) {
      // broadcast(...) //TODO
    }

    private void sendReady() {
      try {
        send(blackClient, READY, BLACK, whiteClient.getName(), Integer.toString(dimension));
        send(whiteClient, READY, WHITE, blackClient.getName(), Integer.toString(dimension));
      } catch (MalformedArgumentsException ignored) {
      }
    }

    private void broadcast(Keyword keyword, String... arguments)
        throws MalformedArgumentsException {
      for (Client client : clients) {
        client.getPeer().send(Protocol.validateAndFormatCommandString(keyword, arguments));
      }
    }

    private void send(Client client, Keyword keyword, String... arguments)
        throws MalformedArgumentsException {
      client.getPeer().send(Protocol.validateAndFormatCommandString(keyword, arguments));
    }

    @Override
    public void update(Observable o, Object arg) {
      if (o instanceof Go) {
        Go go = (Go) o;
        if (arg instanceof Board) {
          Board board = (Board) arg;
          System.out.println();
          System.out.println(board);
          System.out.println();

        } else if (arg instanceof Move) {
          Move move = (Move) arg;

        } else if (arg instanceof MoveType) {
          MoveType moveType = (MoveType) arg;
        }
      }
    }

    public void start() {
      thread.start();
    }
  }
}
