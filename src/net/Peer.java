package net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.net.Socket;
import java.util.Scanner;

/** Created by erik.huizinga on 2-2-17. */
public class Peer {

  private final Socket socket;
  private final BufferedReader reader;
  private final PrintStream out;
  private boolean keepRunning;

  public Peer(Socket socket) {
    this.socket = socket;

    // Set I/O
    BufferedReader reader = null;
    PrintStream printStream = null;
    try {
      reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
      printStream = new PrintStream(socket.getOutputStream(), true);
    } catch (IOException e) {
      e.printStackTrace();
    }
    this.reader = reader;
    out = printStream;
  }

  public BufferedReader getReader() {
    return reader;
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
  }

  void send(String command) {
    out.println(command);
  }
}
