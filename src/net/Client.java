package net;

import java.io.IOException;
import java.net.Socket;

/** Created by erik.huizinga on 2-2-17. */
public class Client {

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
    peer = new Peer(name, socket);
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
    client.stopPeer();
  }

  private void startClient() {
    peer.startPeer();
  }

  private void stopPeer() {
    peer.shutDown();
  }
}