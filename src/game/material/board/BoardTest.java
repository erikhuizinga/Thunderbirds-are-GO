package game.material.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import game.material.PositionedMaterial;
import game.material.PositionedStone;
import game.material.Stone;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Created by erik.huizinga on 25-1-17. */
public class BoardTest {

  private Board board5;
  private Board board5Copy;
  private Stone blackStone;
  private PositionedStone blackPositionedStone00;
  private Feature emptyFeature;
  private PositionedFeature emptyPositionedFeature00;
  private int playableX0 = 0;
  private int playableY0 = 0;
  private PositionedStone blackPositionedStone11;
  private int playableX1 = 1;
  private int playableY1 = 1;
  private List<PositionedMaterial> expectedNeighbors;

  @BeforeEach
  void setUp() {
    board5 = new Board(5);
    blackStone = Stone.BLACK;
    emptyFeature = Feature.EMPTY;
    blackPositionedStone00 = new PositionedStone(playableX0, playableY0, blackStone);
    emptyPositionedFeature00 = new PositionedFeature(playableX0, playableY0, emptyFeature);
    blackPositionedStone11 = new PositionedStone(playableX1, playableY1, blackStone);
  }

  @Test
  void putGet() {
    board5.put(blackPositionedStone00);
    assertEquals(blackStone, board5.get(playableX0, playableY0));
  }

  @Test
  void copy() {
    board5Copy = new Board(board5);
    assertNotEquals(board5, board5Copy);
    assertEquals(board5.get(playableX0, playableY0), board5Copy.get(playableX0, playableY0));
    board5Copy.put(blackPositionedStone00);
    assertNotEquals(board5.get(playableX0, playableY0), board5Copy.get(playableX0, playableY0));
  }

  @Test
  void testToString() {
    String board5FilePath = "src/game/material/board/BoardTestString5.txt";
    String board5MoveFilePath = "src/game/material/board/BoardTestString5Move.txt";
    Board board5Move = new Board(board5);
    for (int i = 0; i < board5Move.getDim(); i++) {
      board5Move.put(new PositionedStone(i, i, blackStone));
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
    board5Copy = new Board(board5);
    int pre = board5.hashCode();
    int preCopy = board5Copy.hashCode();

    // Assert hashcode equality of the unequal boards
    assertEquals(pre, preCopy);

    // Play a move on the board
    board5.put(blackPositionedStone00);
    int post = board5.hashCode();

    // Assert inequality of the pre and post move hash codes
    assertNotEquals(pre, post);

    // Assert equality of the two different boards' hash codes when their layouts are equal
    board5Copy.put(blackPositionedStone00);
    int postCopy = board5Copy.hashCode();
    assertEquals(post, postCopy);

    // Assert equality of the hash codes of an identical board layout that changed intermediately
    board5.put(emptyPositionedFeature00);
    int pre2 = board5.hashCode();
    assertEquals(pre, pre2);

    // Assert inequality of two boards with different layouts
    board5.put(blackPositionedStone11);
    assertNotEquals(board5.hashCode(), board5Copy.hashCode());

    // Assert inequality of the hashcodes of two boards with different sizes, but identical layouts
    Board board7 = new Board(7);
    Board board5 = new Board(5);
    assertNotEquals(board5.hashCode(), board7.hashCode());

    /* Assert inequality of the hashcodes of two boards of different sizes with the same stone
     * played on the same coordinates.
     */
    board5.put(blackPositionedStone00);
    board7.put(blackPositionedStone00);
    assertNotEquals(board5.hashCode(), board7.hashCode());
  }

  @Test
  void getNeighbors() {
    // Set up a board with some neighbours
    board5.put(blackPositionedStone00);
    board5.put(blackPositionedStone11);
    // Now (1, 0) has two stones, an empty field and a side as its neighbors

    PositionedMaterial emptyPositionedMaterial10 =
        new PositionedFeature(1, 0, (Feature) board5.get(1, 0));
    List<PositionedMaterial> neighbors = board5.getNeighbors(emptyPositionedMaterial10);
    // The following works with both the actual Material object and a new Material object
    PositionedMaterial empty20 = new PositionedFeature(2, 0, (Feature) board5.get(2, 0));
    PositionedMaterial side1m1 = new PositionedFeature(1, -1, Feature.SIDE);
    System.out.println(board5);
    expectedNeighbors =
        Arrays.asList(blackPositionedStone00, blackPositionedStone11, empty20, side1m1);
    //    assertEquals(expectedNeighbors, neighbors);
    for (int i = 0; i < expectedNeighbors.size(); i++) {
      assertTrue(neighbors.get(i).containsMaterial(expectedNeighbors.get(i)));
    }
  }
}
