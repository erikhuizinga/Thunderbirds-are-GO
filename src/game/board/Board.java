package game.board;

import game.board.content.Grid;

/**
 * Created by erik.huizinga on 23-1-17.
 */
public class Board {

  private final int dim;
  private final Grid grid;

  Board(int boardSideLength) {
    this.dim = boardSideLength;
    grid = new Grid(boardSideLength);
  }

  public Grid getGrid() {
    return grid;
  }

  public int getDim() {
    return dim;
  }


}
