package game.board.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A grid with contents on a Go game board.
 * Created by erik.huizinga on 23-1-17.
 */
public class Grid {

  /**
   * The map of subscript indices ({@code Arrays.asList(x, y)}) to linear indices (horizontally
   * incremental from the top left to bottom right), with {@code x} horizontally incremental from
   * the left and {@code y} vertically incremental from the top. All indices range from 0 to the
   * playable grid dimension plus two (see {@code {@link Grid}}).
   */
  private final Map<List<Integer>, Integer> sub2IndMap = new HashMap<>();

  /**
   * The map of linear indices to subscript indices.
   */
  private final Map<Integer, List<Integer>> ind2SubMap = new HashMap<>();

  /**
   * The full grid, a {@code Map} of {@code Integer} linear indices with {@code Content}s.
   */
  private final Map<Integer, Content> grid = new HashMap<>();

  private final int dim;

  /**
   * Construct a square {@code Grid} with single-side dimensions as specified. The grid contains the
   * playable part of the game board, initialised as {@code Point.EMPTY}, as well as the surrounding
   * sides, initialised as {@code Point.SIDE}. Therefore, the playable grid has the dimension as
   * specified, but with the boundaries included the full grid single-side dimension is the playable
   * single-side dimension plus two.
   *
   * @param dim the playable grid single-side dimension.
   */
  public Grid(int dim) {
    if (dim <= 0) {
      throw new AssertionError("dim must be greater than zero");
    }
    this.dim = dim;
    init();
  }

  /**
   * @return the single-side dimension of the playable grid.
   */
  public int getDim() {
    return dim;
  }

  /**
   * @return the full grid.
   */
  private Map<Integer, Content> getGrid() {
    return grid;
  }

  /**
   * Initialise the {@code Grid} by setting the content of the playable grid to {@code Point.EMPTY},
   * the content of the boundaries to {@code Point.SIDE} and initialise the {@code sub2Ind} and
   * {@code ind2Sub} methods.
   */
  protected void init() {
    int row;
    int col;
    for (int ind = 0; ind < fullDim() * fullDim(); ind++) {
      row = ind / fullDim();
      col = ind % fullDim();

      // Check if on boundary of full grid or playable grid
      if (isBoundary(ind)) {
        // Boundary of full grid
        getGrid().put(ind, new Point(Point.SIDE));
      } else {
        // Playable grid
        getGrid().put(ind, new Point(Point.EMPTY));
      }

      // Store all the indices!
      List<Integer> sub = new ArrayList<>();
      sub.add(row);
      sub.add(col);
      ind2SubMap.put(ind, sub);
      sub2IndMap.put(sub, ind);
    }
  }

  /**
   * @return the full grid single-side dimension.
   */
  private int fullDim() {
    return getDim() + 2;
  }

  /**
   * Determine if the specified linear index is on a boundary, i.e., outside the playable grid.
   * @param ind the linear index.
   * @return {@code true} if on a boundary; {@code false} otherwise.
   */
  private boolean isBoundary(int ind) {
    List<Integer> sub = ind2Sub(ind);
    int row = sub.get(0);
    int col = sub.get(1);
    return row == 0 || row == fullDim() || col == 0 || col == fullDim();
  }

  /**
   * Get the {@code Content} of the {@code Grid} at the specified linear index.
   *
   * @param ind the linear index.
   * @return the {@code Content}.
   */
  public Content get(int ind) {
    return getGrid().get(ind);
  }

  /**
   * Get the linear index on the grid from the subscript indices.
   *
   * @param sub the subscript indices.
   * @return the linear index.
   */
  public int sub2Ind(List<Integer> sub) {
    return (sub2IndMap.containsKey(sub)) ? sub2IndMap.get(sub) : -1;
  }

  /**
   * Get the subscript indices on the grid from the linear index.
   *
   * @param ind the linear index.
   * @return the subscript indices.
   */
  public List<Integer> ind2Sub(int ind) {
    return (ind2SubMap.containsKey(ind)) ? ind2SubMap.get(ind) : Arrays.asList(-1, -1);
  }

  public int playable2Ind(List<Integer> playable) {
    return -1;  //TODO
  }
}
