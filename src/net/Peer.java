package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

/** Created by erik.huizinga on 2-2-17. */
public class Peer implements Runnable {

  private final Socket socket;
  private final Scanner scanner;
  private final PrintStream out;
  private final Thread thread = new Thread(this);
  private boolean keepRunning;

  public Peer(Socket socket) {
    this.socket = socket;

    // Set I/O
    Scanner scanner = null;
    PrintStream printStream = null;
    try {
      scanner = new Scanner(new BufferedReader(new InputStreamReader(socket.getInputStream())));
      printStream = new PrintStream(socket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.scanner = scanner;
    out = printStream;
  }

  public Scanner getScanner() {
    return scanner;
  }

  @Override
  public void run() {
  }

  void shutDown() {
    try {
      keepRunning = false;
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public void println(String string) {
    System.out.println(string);
  }

  void startPeer() {
    keepRunning = true;
    thread.start();
  }

  void send(String command) {
    out.println(command);
  }
}
