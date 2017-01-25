package game.board;

import game.board.content.Content;
import game.board.content.Point;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * A grid with contents to be used on a Go game board.
 * Created by erik.huizinga on 23-1-17.
 */
public class Grid {

  /**
   * The map of subscript indices ({@code Arrays.asList(x, y)}) to linear indices (horizontally
   * incremental from the top left to bottom right), with {@code x} horizontally incremental from
   * the left and {@code y} vertically incremental from the top. All indices range from 0 to the
   * playable grid dimension plus two (see {@code Grid}).
   */
  private final Map<List<Integer>, Integer> sub2IndMap;

  /**
   * The map of linear indices to subscript indices.
   */
  private final Map<Integer, List<Integer>> ind2SubMap;

  /**
   * The full grid, a {@code Map} of {@code Integer} linear indices with {@code Content}s.
   */
  private final Map<Integer, Content> grid;

  /**
   * The neighbour map, containing the linear indices to the four neighbours as value to the linear
   * index keys.
   */
  private final Map<Integer, List<Integer>> neighbors;

  /**
   * The square playable grid single-side dimension.
   */
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
    sub2IndMap = new HashMap<>();
    ind2SubMap = new HashMap<>();
    grid = new HashMap<>();
    neighbors = new HashMap<>();
    init();
  }

  /**
   * Instantiate a new {@code Grid} as a copy of another.
   *
   * @param grid the grid to copy.
   */
  public Grid(Grid grid) {
    dim = grid.getDim();
    sub2IndMap = grid.getSub2IndMap();
    ind2SubMap = grid.getInd2SubMap();
    this.grid = new HashMap<>(grid.getGrid());
    neighbors = grid.getNeighbors();
  }

  /**
   * @return the sub 2 ind map
   */
  private Map<List<Integer>, Integer> getSub2IndMap() {
    return sub2IndMap;
  }

  /**
   * @return the ind 2 sub map
   */
  private Map<Integer, List<Integer>> getInd2SubMap() {
    return ind2SubMap;
  }

  public Map<Integer, List<Integer>> getNeighbors() {
    return neighbors;
  }

  /**
   * @return the single-side dimension of the playable grid.
   */
  private int getDim() {
    return dim;
  }

  /**
   * @return the full grid.
   */
  private Map<Integer, Content> getGrid() {
    return grid;
  }

  /**
   * Put the specified {@code Content} in the full grid at the specified linear index.
   *
   * @param ind the linear index.
   * @param content the {@code Content}.
   */
  public void put(int ind, Content content) {
    getGrid().put(ind, content);
  }

  /**
   * Initialise the {@code Grid} by setting the content of the playable grid to {@code Point.EMPTY},
   * the content of the boundaries to {@code Point.SIDE} and initialise the {@code sub2Ind} and
   * {@code ind2Sub} methods.
   */
  private void init() {
    for (int ind = 0; ind < getFullDim() * getFullDim(); ind++) {
      // Store all the indices!
      List<Integer> sub = ind2RowCol(ind);
      ind2SubMap.put(ind, sub);
      sub2IndMap.put(sub, ind);

      // Check location of current index and put content on the full grid
      if (isBoundary(sub)) {
        // Boundary of full grid
        getGrid().put(ind, new Point(Point.SIDE));
      } else {
        // Playable grid
        getGrid().put(ind, new Point(Point.EMPTY));
      }
    }
    for (int ind = 0; ind < getFullDim() * getFullDim(); ind++) {
      // Store all the neighbour indices!
      List<Integer> sub = ind2RowCol(ind);
      if (!isBoundary(sub)) {
        getNeighbors().put(ind, findNeighbors(sub));
      }
    }
  }

  /**
   * @return the full grid single-side dimension.
   */
  private int getFullDim() {
    return getDim() + 2;
  }

  /**
   * Get the full grid's subscript indices based on the specified linear index.
   *
   * @param ind the linear index.
   * @return the subscript indices.
   */
  private List<Integer> ind2RowCol(int ind) {
    return Arrays.asList( /* row */ ind / getFullDim(), /* col */ ind % getFullDim());
  }

  /**
   * Get the linear indices to the four neighbours on the full grid of the specified subscript
   * indices.
   *
   * @param sub the subscript indices.
   * @return the four linear indices to the neighbours.
   */
  private List<Integer> findNeighbors(List<Integer> sub) {
    int row = sub2Row(sub);
    int col = sub2Col(sub);
    return Arrays.asList(
        sub2Ind(Arrays.asList(row - 1, col)),  // North
        sub2Ind(Arrays.asList(row, col + 1)),  // East
        sub2Ind(Arrays.asList(row + 1, col)),  // South
        sub2Ind(Arrays.asList(row, col - 1))   // West
    );
  }

  /**
   * Determine if the specified subscript indices point to a boundary of the full grid, i.e.,
   * outside the playable grid.
   *
   * @param sub the subscript indices.
   * @return {@code true} if on a boundary; {@code false} otherwise.
   */
  private boolean isBoundary(List<Integer> sub) {
    int row = sub2Row(sub);
    int col = sub2Col(sub);
    return row == 0 || row == getFullDim() - 1 || col == 0 || col == getFullDim() - 1;
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

  /**
   * Get the full grid linear index from the specified playable grid subscript indices.
   *
   * @param playable the playable subscript indices.
   * @return the full grid linear index.
   */
  public int playable2Ind(List<Integer> playable) {
    for (int e : playable) {
      if (!(e >= 0 && e < getDim())) {
        throw new AssertionError("playable indices out of bounds");
      }
    }
    UnaryOperator<Integer> plusplus = a -> a + 1;
    playable.replaceAll(plusplus);
    return sub2Ind(playable);
  }

  /**
   * @param sub the subscript indices.
   * @return the row subscript index
   */
  private int sub2Row(List<Integer> sub) {
    return sub.get(0);
  }

  /**
   * @param sub the subscript indices.
   * @return the column subscript index
   */
  private int sub2Col(List<Integer> sub) {
    return sub.get(1);
  }
}
