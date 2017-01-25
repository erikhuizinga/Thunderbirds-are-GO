package game.board;

import java.util.Observable;

/**
 * A Go board.
 * Created by erik.huizinga on 23-1-17.
 */
public class Board extends Observable {

  /**
   * The single-side dimension of the playable square grid.
   */
  private final int dim;

  /**
   * The grid in which the board's content is stored.
   */
  private final Grid grid;

  /**
   * Construct a Go board with the specified single-side dimension of the playable grid.
   *
   * @param dim the dimension.
   */
  Board(int dim) {
    this.dim = dim;
    grid = new Grid(dim);
  }

  /**
   * @return the single-side dimension of the playable square grid.
   */
  public int getDim() {
    return dim;
  }
}
