package game.board;

/**
 * A Go board.
 * Created by erik.huizinga on 23-1-17.
 */
public class Board {

  /**
   * The single-side dimension of the playable square grid.
   */
  private final int dim;

  /**
   * The grid in which the board's content is stored.
   */
  private Grid grid;

  /**
   * Construct a Go board with the specified single-side dimension of the playable grid.
   *
   * @param dim the dimension.
   */
  Board(int dim) {
    this.dim = dim;
    setGrid(new Grid(dim));
  }

  /**
   * Instantiate a new {@code Board} as a copy of another.
   *
   * @param board the board to copy.
   */
  Board(Board board) {
    dim = board.getDim();
    setGrid(new Grid(board.getGrid()));
  }

  /**
   * @return the grid
   */
  private Grid getGrid() {
    return grid;
  }

  /**
   * @param grid the grid
   */
  private void setGrid(Grid grid) {
    this.grid = grid;
  }

  /**
   * @return the single-side dimension of the playable square grid.
   */
  public int getDim() {
    return dim;
  }
}
