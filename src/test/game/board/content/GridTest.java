package test.game.board.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import game.board.Grid;
import game.board.content.Point;
import game.board.content.Stone;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Created by erik.huizinga on 24-1-17.
 */
class GridTest {

  private Grid grid1;
  private List<Integer> sub1;
  private int ind1;
  private Grid grid2;
  private List<Integer> sub2;
  private int ind2;
  private Grid grid3;
  private List<Integer> sub3;
  private int ind3;
  private int failInd;
  private List<Integer> failSub;

  @BeforeEach
  void setUp() {
    grid1 = new Grid(1);
    sub1 = Arrays.asList(0, 0);
    ind1 = 0;

    grid2 = new Grid(2);
    sub2 = Arrays.asList(1, 1);
    ind2 = 5;

    grid3 = new Grid(3);
    sub3 = Arrays.asList(2, 2);
    ind3 = 12;

    failInd = -1;
    failSub = Arrays.asList(-1, -1);
  }

  @Test
  void testSub2Ind() {
    assertEquals(ind1, grid1.sub2Ind(sub1));
    assertEquals(ind1, grid2.sub2Ind(sub1));
    assertEquals(ind2, grid2.sub2Ind(sub2));
    assertEquals(ind1, grid3.sub2Ind(sub1));
    assertEquals(ind3, grid3.sub2Ind(sub3));
    assertEquals(failInd, grid1.sub2Ind(Arrays.asList(100, 100)));
  }

  @Test
  void testInd2Sub() {
    assertEquals(sub1, grid1.ind2Sub(ind1));
    assertEquals(sub1, grid2.ind2Sub(ind1));
    assertEquals(sub2, grid2.ind2Sub(ind2));
    assertEquals(sub1, grid3.ind2Sub(ind1));
    assertEquals(sub3, grid3.ind2Sub(ind3));
    assertEquals(failSub, grid1.ind2Sub(100));
  }

  @Test
  void testPlayable2Ind() {
    assertEquals(4, grid1.playable2Ind(Arrays.asList(0, 0)));
    assertEquals(5, grid2.playable2Ind(Arrays.asList(0, 0)));
    assertEquals(18, grid3.playable2Ind(Arrays.asList(2, 2)));
    assertThrows(AssertionError.class, () -> grid1.playable2Ind(Arrays.asList(1, 1)));
    assertThrows(AssertionError.class, () -> grid1.playable2Ind(failSub));
  }

  @Test
  void testInit() {
    assertThrows(AssertionError.class, () -> new Grid(0));
    assertEquals(Point.SIDE, grid1.get(0).getContent());
    assertEquals(Point.EMPTY, grid1.get(4).getContent());
    assertNull(grid1.get(100));
  }

  @Test
  void testNeighbors() {
    int result = grid1.getNeighbors().get(4).get(0);
    assertEquals(1, result);
    assertThrows(NullPointerException.class, () -> grid1.getNeighbors().get(5).get(0));
  }

  @Test
  void testGridCopy() {
    Grid grid1Copy = new Grid(grid1);
    assertFalse(grid1.equals(grid1Copy));
    assertTrue(grid1.get(5).equals(grid1Copy.get(5)));
  }

  @Test
  void testPut() {
    Stone black = new Stone(Stone.BLACK);
    Stone white = new Stone(Stone.WHITE);
    grid1.put(4, black);
    assertEquals(black, grid1.get(4));
    assertNotEquals(white, grid1.get(4));
    // Test w/ copy
    Grid grid1Copy = new Grid(grid1);
    assertEquals(black, grid1Copy.get(4));
    grid1Copy.put(4, white);
    assertNotEquals(white, grid1.get(4));
  }
}
