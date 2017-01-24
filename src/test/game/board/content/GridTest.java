package test.game.board.content;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import game.board.content.Grid;
import game.board.content.Point;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Created by erik.huizinga on 24-1-17.
 */
class GridTest {

  Grid grid1 = new Grid(1);
  Grid grid2 = new Grid(2);
  Grid grid3 = new Grid(3);
  List<Integer> sub0 = Arrays.asList(0, 0);
  int ind0 = 0;
  List<Integer> sub2End = Arrays.asList(1, 1);
  int ind2End = 3;
  List<Integer> sub3End = Arrays.asList(2, 2);
  int ind3End = 8;

  @Test
  void testInitAssertion() {
    assertThrows(AssertionError.class, () -> new Grid(0));
  }

  @Test
  void testInit() {
//    assertEquals(Point.EMPTY, grid1.);
  }

  @Test
  void sub2Ind() {
    assertEquals(ind0, grid1.sub2Ind(sub0));
    assertEquals(ind0, grid2.sub2Ind(sub0));
    assertEquals(ind2End, grid2.sub2Ind(sub2End));
    assertEquals(ind0, grid3.sub2Ind(sub0));
    assertEquals(ind3End, grid3.sub2Ind(sub3End));
    assertEquals(-1, grid1.sub2Ind(sub2End));
  }

  @Test
  void testInd2Sub() {
    assertEquals(sub0, grid1.ind2Sub(ind0));
    assertEquals(sub0, grid2.ind2Sub(ind0));
    assertEquals(sub2End, grid2.ind2Sub(ind2End));
    assertEquals(sub0, grid3.ind2Sub(ind0));
    assertEquals(sub3End, grid3.ind2Sub(ind3End));
    assertEquals(Arrays.asList(-1, -1), grid1.ind2Sub(ind2End));
  }
}