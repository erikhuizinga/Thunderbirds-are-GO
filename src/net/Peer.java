package net;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

/** Created by erik.huizinga on 2-2-17. */
public class Peer implements Runnable, Observer {

  // private final Socket socket;
  private final Socket socket;
  private final Scanner in;
  private final PrintStream out;

  public Peer(Socket socket) {
    // this.socket = socket;
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

  public Scanner getIn() {
    return in;
  }

  public Socket getSocket() {
    return socket;
  }

  @Override
  public void run() {
    String line;
    while (in.hasNextLine() && (line = in.nextLine()) != null) {
      println(line);
    }
  }

  public void shutDown() {
    in.close();
    out.close();
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void println(String string) {
    System.out.println(string);
  }

  public void startPeer() {
    new Thread(this).start();
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o instanceof Client && arg instanceof String) {
      out.println((String) arg);
    }
  }
}
