package game.material.board;

import static util.StringTools.repeat;

import game.material.BoardFeature;
import game.material.GameMaterial;
import game.material.Stone;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.UnaryOperator;

/**
 * A grid with contents to be used on a Go game board. Created by erik.huizinga on 23-1-17.
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
   * The full grid, a {@code Map} of {@code Integer} linear indices with {@code GameMaterial}s.
   */
  private final Map<Integer, GameMaterial> grid;

  /**
   * The neighbour map, containing the linear indices to the four neighbours as value to the linear
   * index keys.
   */
  private final Map<Integer, List<Integer>> neighborsMap;

  /**
   * The square playable grid single-side dimension.
   */
  private final int dim;

  /**
   * The maximum number of spaces around an element for padding in the {@code toString} method.
   */
  private final int maxNumSpaces;

  /**
   * The space character used by {@code Grid} in the {@code toString} method.
   */
  public static final String SPACE = " ";

  /**
   * Construct a square {@code Grid} with single-side dimensions as specified. The grid contains the
   * playable part of the game board, initialised as {@code BoardFeature.EMPTY}, as well as the
   * surrounding sides, initialised as {@code BoardFeature.SIDE}. Therefore, the playable grid has
   * the dimension as specified, but with the boundaries included the full grid single-side
   * dimension is the playable single-side dimension plus two.
   *
   * @param dim the playable grid single-side dimension.
   */
  public Grid(int dim) {
    if (dim <= 0) {
      throw new AssertionError("dim must be greater than zero");
    }
    this.dim = dim;
    maxNumSpaces = (int) Math.log10(dim);
    sub2IndMap = new HashMap<>();
    ind2SubMap = new HashMap<>();
    grid = new HashMap<>();
    neighborsMap = new HashMap<>();
    init();
  }

  /**
   * Instantiate a new {@code Grid} as a copy of another.
   *
   * @param grid the grid to copy.
   */
  public Grid(Grid grid) {
    dim = grid.getDim();
    maxNumSpaces = grid.maxNumSpaces;
    sub2IndMap = grid.getSub2IndMap();
    ind2SubMap = grid.getInd2SubMap();
    this.grid = new HashMap<>(grid.getGrid());
    neighborsMap = grid.getNeighborsMap();
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

  public Map<Integer, List<Integer>> getNeighborsMap() {
    return neighborsMap;
  }

  /**
   * @return the single-side dimension of the playable grid.
   */
  private int getDim() {
    return dim;
  }

  /**
   * Gets max num spaces.
   *
   * @return the max num spaces
   */
  public int getMaxNumSpaces() {
    return maxNumSpaces;
  }

  /**
   * @return the full grid.
   */
  private Map<Integer, GameMaterial> getGrid() {
    return grid;
  }

  /**
   * Put the specified {@code GameMaterial} in the full grid at the specified linear index.
   *
   * @param ind the linear index.
   * @param gameMaterial the {@code GameMaterial}.
   */
  public void put(int ind, GameMaterial gameMaterial) {
    getGrid().put(ind, gameMaterial);
  }

  /**
   * Initialise the {@code Grid} by setting the material of the playable grid to {@code
   * BoardFeature.EMPTY}, the material of the boundaries to {@code BoardFeature.SIDE} and initialise
   * the {@code sub2Ind} and {@code ind2Sub} methods.
   */
  private void init() {
    for (int ind = 0; ind < getFullDim() * getFullDim(); ind++) {
      // Store all the indices!
      List<Integer> sub = ind2RowCol(ind);
      ind2SubMap.put(ind, sub);
      sub2IndMap.put(sub, ind);

      // Check location of current index and put material on the full grid
      if (isBoundary(sub)) {
        // Boundary of full grid
        getGrid().put(ind, BoardFeature.SIDE);
      } else {
        // Playable grid
        getGrid().put(ind, BoardFeature.EMPTY);
      }
    }
    for (int ind = 0; ind < getFullDim() * getFullDim(); ind++) {
      // Store all the neighbour indices!
      List<Integer> sub = ind2RowCol(ind);
      if (!isBoundary(sub)) {
        getNeighborsMap().put(ind, findNeighbors(sub));
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
    return Arrays.asList(/* row */ ind / getFullDim(), /* col */ ind % getFullDim());
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
        sub2Ind(Arrays.asList(row - 1, col)), // North
        sub2Ind(Arrays.asList(row, col + 1)), // East
        sub2Ind(Arrays.asList(row + 1, col)), // South
        sub2Ind(Arrays.asList(row, col - 1)) // West
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
   * Get the {@code GameMaterial} of the {@code Grid} at the specified linear index.
   *
   * @param ind the linear index.
   * @return the {@code GameMaterial}.
   */
  public GameMaterial get(int ind) {
    return getGrid().get(ind);
  }

  /**
   * Get the linear index on the grid from the subscript indices.
   *
   * @param sub the subscript indices.
   * @return the linear index.
   */
  int sub2Ind(List<Integer> sub) {
    return (sub2IndMap.containsKey(sub)) ? sub2IndMap.get(sub) : -1;
  }

  /**
   * Get the subscript indices on the grid from the linear index.
   *
   * @param ind the linear index.
   * @return the subscript indices.
   */
  List<Integer> ind2Sub(int ind) {
    return (ind2SubMap.containsKey(ind)) ? ind2SubMap.get(ind) : Arrays.asList(-1, -1);
  }

  /**
   * Get the full grid linear index from the specified playable grid subscript indices.
   *
   * @param playable the playable subscript indices.
   * @return the full grid linear index.
   */
  int playable2Ind(List<Integer> playable) throws AssertionError {
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

  /**
   * @return the {@code Grid} represented as a {@code String}.
   */
  @Override
  public String toString() {
    // Preallocate variables
    String string = "";
    String side = BoardFeature.SIDE.toString();
    String empty = BoardFeature.EMPTY.toString();
    String black = Stone.BLACK.toString();
    String white = Stone.WHITE.toString();
    List<Integer> sub;
    int row;
    int col;
    int spaces2Prepend;
    int spaces2Append;
    /*
     * Example of spacing: the board dimensions are 19x19, so getDim() returns 19. This number
     * requires two characters to be printed. The smallest number to print always is 1. This number
     * would need to be padded with one additional space to align it correctly with the numbers of
     * greater order of magnitude.
     *
     * Note: one space is always appended to any number to space them correctly.
     *
     * Output of an example board with getDim() == 10 (with ␠ denoting horizontal whitespace) and a
     * few stones played:
     * ␠␠␠1␠␠2␠␠3␠␠4␠␠5␠␠6␠␠7␠␠8␠␠9␠␠10␠␠\n
     * ␠1␠·␠␠●␠␠·␠␠○␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠1\n
     * ␠2␠●␠␠·␠␠●␠␠○␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠2\n
     * ␠3␠·␠␠●␠␠·␠␠○␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠3\n
     * ␠4␠·␠␠·␠␠○␠␠○␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠4\n
     * ␠5␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠5\n
     * ␠6␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠6\n
     * ␠7␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠7\n
     * ␠8␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠8\n
     * ␠9␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠9\n
     * 10␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠␠·␠10\n
     * ␠␠␠1␠␠2␠␠3␠␠4␠␠5␠␠6␠␠7␠␠8␠␠9␠␠10␠␠\n
     *
     * If the board size becomes of order of magnitude greater than 1, then spaces will be appended
     * and prepended in an alternating pattern to numbers of smaller order of magnitude, e.g. ␠1␠␠,
     * ␠10␠ and 100␠
     */

    for (Entry<Integer, List<Integer>> entry : getInd2SubMap().entrySet()) {
      // Prepare iteration variables
      int ind = entry.getKey();
      sub = entry.getValue();
      row = sub2Row(sub);
      col = sub2Col(sub);

      if (isBoundary(sub)) { // Set boundary numbers
        if (row == 0 || row == getDim() + 1) { // Set column number on first and last row
          if (col == 0 || col == getDim() + 1) {
            string += generateGameMaterialString(BoardFeature.SIDE);

          } else {
            spaces2Prepend = prependFunction(col);
            spaces2Append = appendFunction(col);
            string += generateGridString(Integer.toString(col), spaces2Prepend, spaces2Append);
          }

        } else { // Set row number on first and last column
          spaces2Prepend = prependFunction(row);
          spaces2Append = appendFunction(row);
          string += generateGridString(Integer.toString(row), spaces2Prepend, spaces2Append);
        }
      } else { // Set playable field items
        string += generateGameMaterialString(get(ind));
      }

      if (ind < getFullDim() * getFullDim() - 1) { // Not last index
        if (col == getDim() + 1) { // Add a new line character at the end of every row
          string += "\n";
        } else { // Add a space between every column
          string += SPACE;
        }
      }
    }
    return string;
  }

  /**
   * Generate a {@code toString} block for a {@code BoardFeature.SIDE}.
   */
  private String generateGameMaterialString(GameMaterial material) {
    String string = material.toString();
    int spaces2Prepend = prependFunction(string.length());
    int spaces2Append = appendFunction(string.length());
    return generateGridString(string, spaces2Prepend, spaces2Append);
  }

  /**
   * Create one building block for the {@code toString} method.
   *
   * @param element the {@code String} element to put in the block.
   * @param spaces2Prepend the number of spaces to prepend.
   * @param spaces2Append the number of spaces to append.
   * @return the block.
   */
  private String generateGridString(String element, int spaces2Prepend, int spaces2Append) {
    String string = "";
    string += repeat(SPACE, spaces2Prepend);
    string += element;
    string += repeat(SPACE, spaces2Append);
    return string;
  }

  /**
   * Calculate the number of spaces to prepend to an element in a block for the {@code toString}
   * method.
   *
   * @param num the number to calculate the order of.
   * @return the number of spaces to prepend.
   */
  private int prependFunction(int num) {
    return (getMaxNumSpaces() - ((int) Math.log10(num)) + 1) / 2;
  }

  /**
   * Calculate the number of spaces to append to an element in a block for the {@code toString}
   * method.
   *
   * @param num the number to calculate the order of.
   * @return the number of spaces to append.
   */
  private int appendFunction(int num) {
    return (getMaxNumSpaces() - ((int) Math.log10(num))) / 2;
  }
}
