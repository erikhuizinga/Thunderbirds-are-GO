package net;

import static net.Protocol.Keyword.CANCEL;
import static net.Protocol.Keyword.CHAT;
import static net.Protocol.Keyword.GO;
import static net.Protocol.Keyword.PLAYER;
import static net.Protocol.Keyword.READY;
import static net.Protocol.Keyword.WAITING;
import static net.Protocol.Keyword.WARNING;
import static net.Protocol.SPACE;

import game.material.Stone;
import java.io.IOException;
import java.net.Socket;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import net.Protocol.Command;
import net.Protocol.Keyword;
import net.Protocol.MalformedArgumentsException;
import net.Protocol.UnexpectedKeywordException;
import util.Strings;

/** Created by erik.huizinga on 2-2-17. */
public class Client {

  public static final String USAGE =
      "usage: java " + Client.class.getName() + " <name> <address> <port>";

  private final String name;
  private final Peer peer;
  private final Scanner in;
  private boolean isReady = false;
  private boolean isCancelled = false;

  private Client(String name, String address, int port) {
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

  private void startClient() {
    // Start peer thread and play
    peer.startPeer();
    try {
      play();
    } catch (MalformedArgumentsException | UnexpectedKeywordException e) {
      e.printStackTrace(); //TODO delete me
      cancel();
    }
  }

  private Command expect(Keyword... expectedKeywords)
      throws UnexpectedKeywordException, MalformedArgumentsException {
    List<Command> expectedCommandList =
        Arrays.stream(expectedKeywords).map(Command::new).collect(Collectors.toList());

    Command chatCommand = new Command(CHAT);
    chatCommand.setExecutable(Protocol::chatPrinter);
    expectedCommandList.add(chatCommand);

    Command warningCommand = new Command(WARNING);
    warningCommand.setExecutable(System.out::println);
    expectedCommandList.add(warningCommand);

    return Protocol.expect(in, expectedCommandList.toArray(new Command[] {}));
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
    int dimension = getBoardDimension();
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
    String color = argList.get(0);
    String opponentName = argList.get(1);
    System.out.println("Ready to play GO!");
    System.out.println(
        "Your colour is "
            + Enum.valueOf(Stone.class, color.toUpperCase())
            + " "
            + color
            + "."); // TODO create argument getters depending on Keyword
    System.out.println("Your opponent is " + opponentName + ".");
    isReady = true;
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

  private void stopClient() {
    peer.shutDown();
  }

  public String getName() {
    return name;
  }
}
