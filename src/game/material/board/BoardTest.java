package game.material.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import game.material.Stone;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Created by erik.huizinga on 25-1-17. */
public class BoardTest {

  private Board board5;
  private Stone blackStone;
  private int horzPos = 0;
  private int vertPos = 0;

  @BeforeEach
  void setUp() {
    board5 = new Board(5);
    blackStone = Stone.BLACK;
  }

  @Test
  void putGet() {
    board5.put(horzPos, vertPos, blackStone);
    assertEquals(blackStone, board5.get(horzPos, vertPos));
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

  @Test
  void testBoardHistory() {
    Board board5Copy = new Board(board5);
    int pre = board5.hashCode();
    int preCopy = board5Copy.hashCode();

    // Assert hashcode equality of the unequal boards
    assertEquals(pre, preCopy);

    // Play a move on the board
    board5.put(horzPos, vertPos, blackStone);
    int post = board5.hashCode();

    // Assert inequality of the pre and post move hash codes
    assertNotEquals(pre, post);

    // Assert equality of the two different boards' hash codes when their layouts are equal
    board5Copy.put(horzPos, vertPos, blackStone);
    int postCopy = board5Copy.hashCode();
    assertEquals(post, postCopy);

    // Assert equality of the hash codes of an identical board layout that changed intermediately
    board5.put(horzPos, vertPos, Feature.EMPTY);
    int pre2 = board5.hashCode();
    assertEquals(pre, pre2);

    // Assert inequality of two boards with different layouts
    board5.put(horzPos + 1, vertPos + 1, blackStone);
    assertNotEquals(board5.hashCode(), board5Copy.hashCode());

    // Assert inequality of the hashcodes of two boards with different sizes, but identical layouts
    Board board7 = new Board(7);
    Board board5 = new Board(5);
    assertNotEquals(board5.hashCode(), board7.hashCode());

    /* Assert inequality of the hashcodes of two boards of different sizes with the same stone
     * played on the same coordinates.
     */
    board5.put(horzPos, vertPos, blackStone);
    board7.put(horzPos, vertPos, blackStone);
    assertNotEquals(board5.hashCode(), board7.hashCode());
  }
}
