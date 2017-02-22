package net;

import static net.Protocol.Keyword.CANCEL;
import static net.Protocol.Keyword.GO;
import static net.Protocol.Keyword.PLAYER;
import static net.Protocol.Keyword.READY;
import static net.Protocol.Keyword.WAITING;
import static net.Protocol.expect;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;
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

  public static void main(String[] args) {
    if (args.length != 3) {
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

    String address = args[1];
    if (name.equals("") || name.equals(null)) {
      System.out.println("second argument must not be null or empty");
      System.out.println(USAGE);
      System.exit(0);
    }

    int port = Integer.parseInt(args[2]);
    if (name.equals("") || name.equals(null)) {
      System.out.println("third argument must not be null or empty");
      System.out.println(USAGE);
      System.exit(0);
    }

    Client client = new Client(name, address, port);
    client.startClient();
    client.stopClient();
  }

  private void startClient() {
    // Start peer thread and play
    peer.startPeer();
    try {
      play();
    } catch (MalformedArgumentsException | UnexpectedKeywordException e) {
      e.printStackTrace(); //TODO delete me
      try {
        cancel();
      } catch (CancelException ignored) {
      }
      return;
    } catch (CancelException e) {
      return;
    }
  }

  private void play()
      throws MalformedArgumentsException, UnexpectedKeywordException, CancelException {
    System.out.println(
        "While not in game, type CANCEL to cancel the previous request to play a game.");

    // Announce player
    announcePlayer();

    // Announce board dimension
    announceBoardDimension(getBoardDimension());

    Keyword keyword;
    keyword = expect(in, WAITING, READY);
  }

  private int getBoardDimension() throws CancelException {
    String input;
    int dimension = 0;
    System.out.print("On what board dimension do you want to play? ");
    do {
      try {
        input = Strings.readLine("Please enter an odd number between 5 and 131: ");
        if (input.toUpperCase().trim().equals("CANCEL")) {
          cancel();
        }
        dimension = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.err.println("Unable to parse number from input, please try again.");
      }
      //TODO support specifying opponent name
    } while (!Protocol.isValidDimension(dimension));
    return dimension;
  }

  private void announcePlayer() throws MalformedArgumentsException {
    send(PLAYER, name);
  }

  private void cancel() throws CancelException {
    try {
      send(CANCEL);
    } catch (MalformedArgumentsException ignored) {
    }
    throw new CancelException();
  }

  private void announceBoardDimension(int dimension) throws MalformedArgumentsException {
    send(GO, Integer.toString(dimension));
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

  private class CancelException extends Exception {}
}
