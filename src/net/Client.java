package net;

import static net.Protocol.Keyword.CANCEL;
import static net.Protocol.Keyword.CHAT;
import static net.Protocol.Keyword.EXIT;
import static net.Protocol.Keyword.GO;
import static net.Protocol.Keyword.PLAYER;
import static net.Protocol.Keyword.READY;
import static net.Protocol.Keyword.WAITING;
import static net.Protocol.Keyword.WARNING;
import static net.Protocol.SPACE;

import game.Go;
import game.Rules;
import game.action.Move;
import game.action.Move.MoveType;
import game.material.Stone;
import game.material.board.Board;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import java.util.stream.Collectors;
import net.Protocol.Command;
import net.Protocol.Keyword;
import net.Protocol.MalformedArgumentsException;
import net.Protocol.UnexpectedKeywordException;
import players.HumanPlayer;
import players.Player;
import players.RemotePlayer;
import ui.tui.TUI;
import util.Strings;

/** Created by erik.huizinga on 2-2-17. */
public class Client implements Observer {

  public static final String USAGE =
      "usage: java " + Client.class.getName() + " <name> <address> <port>";

  private final String name;
  private final Peer peer;
  private final Scanner in;
  private boolean isReady = false;
  private boolean isCancelled = false;
  private int dimension;
  private Stone stone;
  private TUI tui = null;

  public Client(String name, String address, int port) {
    this.name = name;

    // Set peer
    Socket socket = null;
    try {
      socket = new Socket(address, port);
    } catch (IOException e) {
      e.printStackTrace();
    }
    peer = new Peer(socket);
    in = peer.getScanner();
  }

  protected Client(String name, Peer peer) {
    this.name = name;
    this.peer = peer;
    this.in = peer.getScanner();
  }

  public Client(String name, String address, int port, TUI tui, int dimension) {
    this(name, address, port);
    this.tui = tui;
    this.dimension = dimension;
  }

  public static void main(String[] args) {
    if (args.length != 3) {
      System.err.println("incorrect number of input arguments");
      System.out.println(USAGE);
      System.exit(0);
    }

    String name = args[0];
    if (name == null || name.equals("")) {
      System.err.println("first argument must not be null or empty");
      System.out.println(USAGE);
      System.exit(0);
    }

    String address = args[1];
    if (address == null || address.equals("")) {
      System.err.println("second argument must not be null or empty");
      System.out.println(USAGE);
      System.exit(0);
    }

    int port = -1;
    try {
      port = Integer.parseInt(args[2]);
    } catch (NumberFormatException e) {
      System.err.println("third argument must be a valid port number");
      System.out.println(USAGE);
      System.exit(0);
    }
    if (port < 0 || port > 65535) {
      System.err.println("third argument must be a port number between 0 and 65535, inclusive");
      System.out.println(USAGE);
      System.exit(0);
    }

    Client client = new Client(name, address, port);
    client.startClient();
    client.stopClient();
  }

  public Peer getPeer() {
    return peer;
  }

  public void startClient() {
    // Start peer thread and play
    peer.startPeer();
    try {
      play();
    } catch (MalformedArgumentsException | UnexpectedKeywordException e) {
      e.printStackTrace(); //TODO delete me
      cancel();
    }
  }

  private Command expect(Command... expectedCommands)
      throws UnexpectedKeywordException, MalformedArgumentsException {
    List<Command> expectedCommandList =
        Arrays.stream(expectedCommands).collect(Collectors.toList());

    Command chatCommand = new Command(CHAT);
    chatCommand.setExecutable(Protocol::chatPrinter);
    expectedCommandList.add(chatCommand);

    Command warningCommand = new Command(WARNING);
    warningCommand.setExecutable(System.err::println);
    expectedCommandList.add(warningCommand);
    return Protocol.expect(in, expectedCommandList.toArray(new Command[] {}));
  }

  private void play() throws MalformedArgumentsException, UnexpectedKeywordException {
    System.out.println(
        "While not in game, type:\n"
            + "  CANCEL to cancel the current request to play a game.\n"
            + "  CHAT with a chat message to chat with anybody on the server.\n");

    // Client: PLAYER name
    announcePlayer();

    // Client: GO dimension [opponentName]
    if (!Protocol.isValidDimension(dimension)) {
      dimension = getBoardDimension();
    }
    if (isCancelled) {
      return;
    }
    announceBoardDimension(dimension);

    // Server: WAITING
    Command waitingCommand = new Command(WAITING);
    waitingCommand.setExecutable(argList -> System.out.println("Waiting for another player..."));

    Command readyCommand = new Command(READY);
    readyCommand.setExecutable(this::startGame);

    // Wait for ready signal to play a game
    do {
      expect(waitingCommand, readyCommand).execute();
    } while (!isReady);
  }

  private void announcePlayer() throws MalformedArgumentsException {
    send(PLAYER, name);
  }

  private int getBoardDimension() {
    String input;
    int dimension = 0;
    System.out.println("On what board dimension do you want to play?");
    do {
      input = Strings.readLine("Please enter an odd number between 5 and 131: ");
      String[] inputWords = input.trim().split(SPACE);
      inputWords[0] = inputWords[0].toUpperCase();
      try {
        dimension = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        try {
          switch (Keyword.valueOf(inputWords[0])) {
            case CANCEL:
              cancel();
              break;

            case CHAT:
              chat(Arrays.copyOfRange(inputWords, 1, inputWords.length));
              break;
          }
        } catch (IllegalArgumentException ee) {
          System.err.println("Unable to parse number or command from input, please try again.");
        }
      }
      //TODO support specifying opponent name
    } while (!isCancelled && !Protocol.isValidDimension(dimension));
    return dimension;
  }

  private void announceBoardDimension(int dimension) throws MalformedArgumentsException {
    send(GO, Integer.toString(dimension));
  }

  private void startGame(List<String> argList) {
    isReady = true;
    String color = argList.get(0).toUpperCase();
    stone = Enum.valueOf(Stone.class, color);
    String opponentName = argList.get(1);
    System.out.println("Ready to play GO!");
    System.out.println(
        "Your colour is "
            + stone
            + " "
            + color.toLowerCase()
            + "."); // TODO create argument getters depending on Keyword
    System.out.println("Your opponent is " + opponentName + ".");

    // Instantiate players
    Player blackPlayer;
    Player whitePlayer;
    if (stone == Stone.BLACK) {
      blackPlayer = new HumanPlayer(name, stone);
      whitePlayer = new RemotePlayer(stone.other(), opponentName);
    } else {
      whitePlayer = new HumanPlayer(name, stone);
      blackPlayer = new RemotePlayer(stone.other(), opponentName);
    }

    // Instantiate game
    Go go = new Go(dimension, blackPlayer, whitePlayer);

    // Add observers of the game
    if (tui != null) {
      go.addObserver(tui);
    }
    go.addObserver(this);

    // Start the game
    Thread goThread = new Thread(go);
    goThread.start();

    while (!Rules.isFinished(go)) {
      try {
        Thread.sleep(1000); //TODO reduce time or handle finished game differently
      } catch (InterruptedException ignored) {
      }
    }
  }

  private void chat(String[] words) {
    try {
      send(CHAT, words);
    } catch (MalformedArgumentsException e) {
      System.err.println("CHAT arguments malformed");
    }
  }

  private void cancel() {
    try {
      send(CANCEL);
    } catch (MalformedArgumentsException ignored) {
    }
    isCancelled = true;
  }

  private void send(Keyword keyword, String... arguments) throws MalformedArgumentsException {
    send(Protocol.validateAndFormatCommandString(keyword, arguments));
  }

  private void send(String command) {
    peer.send(command);
  }

  public void stopClient() {
    try {
      send(EXIT);
    } catch (MalformedArgumentsException ignored) {
    }
    peer.shutDown();
  }

  public String getName() {
    return name;
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Go) {
      Go go = (Go) o;
      if (arg instanceof Board) {
        Board board = (Board) arg;

      } else if (arg instanceof Move) {
        Move move = (Move) arg;

      } else if (arg instanceof MoveType) {
        MoveType moveType = (MoveType) arg;
      }
    }
  }
}
