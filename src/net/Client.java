package net;

import static net.Protocol.expect;

import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Observable;
import java.util.Scanner;
import net.Protocol.ClientCommand;
import net.Protocol.MalformedArgumentsException;
import net.Protocol.ProtocolCommand;
import net.Protocol.ServerCommand;
import net.Protocol.UnexpectedCommandException;
import util.Strings;

/** Created by erik.huizinga on 2-2-17. */
public class Client extends Observable {

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
    in = peer.getIn();
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
    addObserver(peer);
    peer.startPeer();
    play();
  }

  private void play() {
    String input = null;
    announcePlayer();

    int dimension = 0;
    System.out.print("On what board dimension do you want to play? ");
    do {
      try {
        input = Strings.readLine("Please enter an odd number between 5 and 131: ");
        dimension = Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.err.println("Unable to parse number from input, please try again.");
      }
      //TODO support cancelling -> ClientCommand.CANCEL
      //TODO support specifying opponent name
    } while (!Protocol.isValidDimension(dimension));
    announceBoardDimension(dimension);

    List<String> commList;
    try {
      commList = expect(in, ServerCommand.WAITING, ServerCommand.READY);

    } catch (UnexpectedCommandException | MalformedArgumentsException e) {
      e.printStackTrace();
    }
  }

  private void announcePlayer() {
    sendCommand(ClientCommand.PLAYER, name);
  }

  private void announceBoardDimension(int dimension) {
    sendCommand(ClientCommand.GO, Integer.toString(dimension));
  }

  private void sendCommand(ProtocolCommand protocolCommand, String... keys) {
    setChanged();
    try {
      notifyObservers(Protocol.validateAndFormatCommandString(protocolCommand, keys));
    } catch (MalformedArgumentsException e) {
      e.printStackTrace();
      stopClient();
    }
  }

  private void stopClient() {
    peer.shutDown();
  }
}
