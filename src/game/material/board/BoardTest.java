package game.material.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import game.material.Stone;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by erik.huizinga on 25-1-17.
 */
class BoardTest {

  private Board board5;
  private Stone blackStone;
  private int x = 0;
  private int y = 0;

  @BeforeEach
  void setUp() {
    board5 = new Board(5);
    blackStone = Stone.BLACK;
  }

  @Test
  void putGet() {
    board5.put(x, y, blackStone);
    assertEquals(blackStone, board5.get(x, y));
  }

  @Test
  void copy() {
    Board board5Copy = new Board(board5);
    assertNotEquals(board5, board5Copy);
    assertEquals(board5.get(0, 0), board5Copy.get(0, 0));
    board5Copy.put(0, 0, blackStone);
    assertNotEquals(board5.get(0, 0), board5Copy.get(0, 0));
  }

  @Test
  void testToString() {
    String board5FilePath = "src/game/material/board/BoardTestString5.txt";
    String board5MoveFilePath = "src/game/material/board/BoardTestString5Move.txt";
    Board board5Move = new Board(board5);
    for (int i = 0; i < board5Move.getDim(); i++) {
      board5Move.put(i, i, blackStone);
    }

    // Write some grids to text files
    /*
    try {
      Writer writer = new PrintWriter(
          new FileOutputStream(board5FilePath));
      writer.write(board5.toString());
      writer.flush();

      writer = new PrintWriter(
          new FileOutputStream(board5MoveFilePath));
      writer.write(board5Move.toString());
      writer.flush();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(2);
    }
    */

    String board5String = "";
    try (Scanner scanner = new Scanner(new FileReader(board5FilePath))) {
      while (scanner.hasNextLine()) {
        board5String += scanner.nextLine() + "\n";
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    String board5MoveString = "";
    try (Scanner scanner = new Scanner(new FileReader(board5MoveFilePath))) {
      while (scanner.hasNextLine()) {
        board5MoveString += scanner.nextLine() + "\n";
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println(board5);
    System.out.println(board5Move);

    board5String = board5String.substring(0, board5String.length() - 1);
    assertEquals(board5String, board5.toString());
    board5MoveString = board5MoveString.substring(0, board5MoveString.length() - 1);
    assertEquals(board5MoveString, board5Move.toString());
  }
}
