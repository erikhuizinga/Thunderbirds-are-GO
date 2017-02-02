package net;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/** Created by erik.huizinga on 2-2-17. */
public class Peer implements Runnable {

  // private final Socket socket;
  private final String name;
  private final Socket socket;
  private final Scanner in;
  private final PrintStream out;

  public Peer(String name, Socket socket) {
    // this.socket = socket;
    this.name = name;
    this.socket = socket;

    // Set I/O
    Scanner scanner = null;
    PrintStream printStream = null;
    try {
      scanner = new Scanner(socket.getInputStream());
      printStream = new PrintStream(socket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    in = scanner;
    out = printStream;
  }

  @Override
  public void run() {}

  public void shutDown() {
    in.close();
    out.close();
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void startPeer() {
    new Thread(this).start();
  }
}
