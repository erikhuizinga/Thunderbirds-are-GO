package game.material.board;

import game.material.GameMaterial;
import game.material.Stone;
import java.util.Arrays;

/** A Go board. Created by erik.huizinga on 23-1-17. */
public class Board {

  /** The single-side dimension of the playable square grid. */
  private final int dim;

  /** The grid in which the board's material is stored. */
  private Grid grid;

  /**
   * Construct a Go board with the specified single-side dimension of the playable grid.
   *
   * @param dim the dimension.
   */
  public Board(int dim) {
    this.dim = dim;
    setGrid(new Grid(dim));
  }

  /**
   * Instantiate a new {@code Board} as a copy of another.
   *
   * @param board the board to copy.
   */
  public Board(Board board) {
    dim = board.getDim();
    setGrid(new Grid(board.getGrid()));
  }

  /** @return the grid */
  private Grid getGrid() {
    return grid;
  }

  /** @param grid the grid */
  private void setGrid(Grid grid) {
    this.grid = grid;
  }

  /** @return the single-side dimension of the playable square grid. */
  public int getDim() {
    return dim;
  }

  /**
   * Put the specified {@code Stone} on the {@code Board} at the specified playable grid indices.
   *
   * @param x the horizontal index of the playable grid.
   * @param y the vertical index of the playable grid.
   * @param stone the {@code Stone}.
   */
  public void put(int x, int y, Stone stone) {
    getGrid().put(getGrid().playable2Ind(Arrays.asList(x, y)), stone);
  }

  /**
   * Get the {@code GameMaterial} on the {@code Board} at the specified playable indices.
   *
   * @param x the horizontal playable index.
   * @param y the vertical playable index.
   * @return the {@code GameMaterial}.
   */
  public GameMaterial get(int x, int y) {
    return getGrid().get(getGrid().playable2Ind(Arrays.asList(x, y)));
  }
}
