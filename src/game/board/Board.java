package game.board;

import game.board.content.Grid;
import java.util.Observable;

/**
 * Created by erik.huizinga on 23-1-17.
 */
public class Board extends Observable {

  private final int dim;
  private final Grid grid;

  Board(int dim) {
    this.dim = dim;
    grid = new Grid(dim);
  }

  public int getDim() {
    return dim;
  }
}
