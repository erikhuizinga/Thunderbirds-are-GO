package net;

import java.io.IOException;
import java.net.Socket;
import java.util.Observable;
import net.Protocol.ClientCommand;
import net.Protocol.MalformedCommandException;
import net.Protocol.ProtocolCommand;
import util.Strings;

/** Created by erik.huizinga on 2-2-17. */
public class Client extends Observable {

  public static final String USAGE =
      "usage: java " + Client.class.getName() + " <name> <address> <port>";

  private final String name;
  private final Peer peer;

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
    announcePlayer();

    int dimension = 0;
    do {
      try {
        dimension =
            Integer.parseInt(
                Strings.readString(
                    "On what board dimension do you want to play? "
                        + "Please enter an odd number between 5 and 131: "));
      } catch (NumberFormatException e) {
        System.out.println("Unable to parse number from input, please try again.");
      }
    } while (!Protocol.isValidDimension(dimension));
    announceBoardDimension(dimension);
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
      notifyObservers(Protocol.validateAndFormatCommand(protocolCommand, keys));
    } catch (MalformedCommandException e) {
      e.printStackTrace();
      stopClient();
    }
  }

  private void stopClient() {
    peer.shutDown();
  }
}
