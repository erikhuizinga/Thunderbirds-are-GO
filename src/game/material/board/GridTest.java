package game.material.board;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import game.material.Stone;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by erik.huizinga on 24-1-17.
 */
public class GridTest {

  private final int ind0 = 0;
  private final int ind4 = 4;
  private final int ind5 = 5;
  private final int ind8 = 8;
  private final int ind12 = 12;
  private final int ind18 = 18;

  private final List<Integer> playable00 = Arrays.asList(0, 0);
  private final List<Integer> playable11 = Arrays.asList(1, 1);
  private final List<Integer> playable22 = Arrays.asList(2, 2);

  private Grid grid1;
  private Grid grid2;
  private Grid grid3;
  private Grid grid19;

  private List<Integer> sub00;
  private List<Integer> sub11;
  private List<Integer> sub22;


  private int failInd = -1;
  private List<Integer> failSub = Arrays.asList(-1, -1);

  @BeforeEach
  void setUp() {
    sub00 = playable00;
    sub11 = playable11;
    sub22 = playable22;

    grid1 = new Grid(1);
    grid2 = new Grid(2);
    grid3 = new Grid(3);
    grid19 = new Grid(19);
  }

  @Test
  void sub2Ind() {
    assertEquals(ind0, grid1.sub2Ind(sub00));
    assertEquals(ind0, grid2.sub2Ind(sub00));
    assertEquals(ind5, grid2.sub2Ind(sub11));
    assertEquals(ind0, grid3.sub2Ind(sub00));
    assertEquals(ind12, grid3.sub2Ind(sub22));
    assertEquals(failInd, grid1.sub2Ind(Arrays.asList(100, 100)));
  }

  @Test
  void ind2Sub() {
    assertEquals(sub00, grid1.ind2Sub(ind0));
    assertEquals(sub00, grid2.ind2Sub(ind0));
    assertEquals(sub11, grid2.ind2Sub(ind5));
    assertEquals(sub00, grid3.ind2Sub(ind0));
    assertEquals(sub22, grid3.ind2Sub(ind12));
    assertEquals(failSub, grid1.ind2Sub(100));
  }

  @Test
  void playable2Ind() {
    assertEquals(ind4, grid1.playable2Ind(playable00));
    assertEquals(ind5, grid2.playable2Ind(playable00));
    assertEquals(ind18, grid3.playable2Ind(playable22));
    assertEquals(ind8, grid1.playable2Ind(playable11));
    assertThrows(AssertionError.class, () -> grid1.playable2Ind(playable22));
  }

  @Test
  void ind2Playable() {
    assertEquals(playable00, grid1.ind2Playable(ind4));
    assertEquals(playable00, grid2.ind2Playable(ind5));
    assertEquals(playable22, grid3.ind2Playable(ind18));
  }

  @Test
  void init() {
    assertThrows(AssertionError.class, () -> new Grid(0));
    assertEquals(Feature.SIDE, grid1.get(0));
    assertEquals(Feature.EMPTY, grid1.get(ind4));
    assertNull(grid1.get(100));
  }

  @Test
  void getNeighborsMap() {
    int result = grid1.getNeighborsMap().get(ind4).get(0);
    assertEquals(1, result);
    assertThrows(NullPointerException.class, () -> grid1.getNeighborsMap().get(ind5).get(0));
  }

  @Test
  void gridCopy() {
    Grid grid1Copy = new Grid(grid1);
    assertFalse(grid1.equals(grid1Copy));
    assertTrue(grid1.get(ind5).equals(grid1Copy.get(ind5)));
  }

  @Test
  void put() {
    Stone black = Stone.BLACK;
    Stone white = Stone.WHITE;
    grid1.put(ind4, black);
    assertEquals(black, grid1.get(ind4));
    assertNotEquals(white, grid1.get(ind4));
    // Test w/ copy
    Grid grid1Copy = new Grid(grid1);
    assertEquals(black, grid1Copy.get(ind4));
    grid1Copy.put(ind4, white);
    assertNotEquals(white, grid1.get(ind4));
  }

  @Test
  void testToString() {
    String grid1FilePath = "src/game/material/board/GridTestString1.txt";
    String grid19FilePath = "src/game/material/board/GridTestString19.txt";

    // Write some grids to text files
    /*
    try {
      Writer writer = new PrintWriter(
          new FileOutputStream(grid1FilePath));
      writer.write(grid1.toString());
      writer.flush();

      writer = new PrintWriter(
          new FileOutputStream(grid19FilePath));
      writer.write(grid19.toString());
      writer.flush();

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    } catch (IOException e) {
      e.printStackTrace();
      System.exit(2);
    }
    */

    String grid1String = "";
    try (Scanner scanner = new Scanner(new FileReader(grid1FilePath))) {
      while (scanner.hasNextLine()) {
        grid1String += scanner.nextLine() + "\n";
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    String grid19String = "";
    try (Scanner scanner = new Scanner(new FileReader(grid19FilePath))) {
      while (scanner.hasNextLine()) {
        grid19String += scanner.nextLine() + "\n";
      }
    } catch (FileNotFoundException e) {
      e.printStackTrace();
      System.exit(1);
    }

    System.out.println(grid1);
    System.out.println(grid19);

    grid1String = grid1String.substring(0, grid1String.length() - 1);
    assertEquals(grid1String, grid1.toString());
    grid19String = grid19String.substring(0, grid19String.length() - 1);
    assertEquals(grid19String, grid19.toString());
  }
}
