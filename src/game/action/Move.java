package game.action;

import game.material.GameMaterial;
import game.material.board.Board;
import game.material.Stone;

/** A move by a player. Created by erik.huizinga on 24-1-17. */
public class Move {

  /** The {@code GameMaterial} being placed. */
  private final GameMaterial material;

  /** The horizontal position on the playable grid. */
  private final int horzPos;

  /** The vertical position on the playable grid. */
  private final int vertPos;

  /**
   * Instantiate a new {@code Move} at the specified position with the specified {@code
   * GameMaterial}.
   *
   * @param horzPos the horizontal position.
   * @param vertPos the vertical position.
   * @param material the {@code GameMaterial}.
   */
  public Move(int horzPos, int vertPos, GameMaterial material) {
    this.horzPos = horzPos;
    this.vertPos = vertPos;
    this.material = material;
  }

  /**
   * Apply this {@code Move} to the specified {@code Board}. The specified {@code Board} is left
   * untouched and a modified copy of the {@code Board} is returned.
   *
   * @param board the {@code Board} to apply the move on.
   * @return board the {@code Board} with the {@code Move} applied to it.
   */
  public Board apply(Board board) {
    Board newBoard = new Board(board);
    newBoard.put(getHorzPos(), getVertPos(), getMaterial());
    return newBoard;
  }

  /** @return the {@code Stone}. */
  public GameMaterial getMaterial() {
    return material;
  }

  /** @return the horizontal position. */
  public int getHorzPos() {
    return horzPos;
  }

  /** @return the vertical position. */
  public int getVertPos() {
    return vertPos;
  }
}
