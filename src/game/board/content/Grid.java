package game.board.content;

import java.util.HashMap;
import java.util.Map;

/**
 * A grid on a Go game board.
 * Created by erik.huizinga on 23-1-17.
 */
public abstract class Grid {

  /**
   * The grid, a {@code Map} of {@code Integer} indices with {@code Content}s.
   */
  private final Map<Integer, Content> grid = new HashMap<>();
  /**
   * The map of subscript indices ({@code {x, y}}) to linear indices (horizontally incremental from
   * the top left to bottom right), with {@code x} horizontally incremental from the left and {@code
   * y} vertically incremental from the top.
   */
  private final Map<int[], Integer> sub2IndMap = new HashMap<>();
  private final Map<Integer, int[]> ind2SubMap = new HashMap<>();

  public Grid(int boardSideLength) {
    init(boardSideLength);
  }

  /**
   * @return the grid.
   */
  public Map<Integer, Content> getGrid() {
    return grid;
  }

  /**
   * Initialise the {@code Grid} by setting all values to {@code Point.EMPTY} and initialise
   * the {@code sub2Ind} and {@code ind2Sub} methods.
   *
   * @param dim The length of the {@code game.board.Board}}.
   */
  public void init(int dim) {
    int sub[] = new int[2];
    for (int ind = 0; ind < dim; ind++) {
      grid.put(ind, new Point(Point.EMPTY));
      sub[0] = ind / dim;  // Get column, i.e., x
      sub[1] = ind % dim;  // Get row, i.e., y
      ind2SubMap.put(ind, sub);
      sub2IndMap.put(sub, ind);
    }
  }

  /**
   * Get the linear index on the grid from the subscript indices.
   *
   * @param sub The subscript indices.
   * @return The linear index.
   */
  public int sub2Ind(int[] sub) {
    return (sub2IndMap.containsKey(sub)) ? sub2IndMap.get(sub) : -1;
  }

  /**
   * get the subscript indices on the grid from the linear index.
   *
   * @param ind The linear index.
   * @return The subscript indices.
   */
  public int[] ind2Sub(int ind) {
    return (ind2SubMap.containsKey(ind)) ? ind2SubMap.get(ind) : new int[]{-1, -1};
  }
}
