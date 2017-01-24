package game.board;

/**
 * Created by erik.huizinga on 23-1-17.
 */
public class Board {

  private final int dim;

  Board(int boardSideLength) {
    this.dim = boardSideLength;
  }

  public int getDim() {
    return dim;
  }
}
