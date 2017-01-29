package game.action;

import game.material.BoardFeature;
import game.material.GameMaterial;
import game.material.board.Board;

/** Created by erik.huizinga on 29-1-17. */
public class Remove extends Move {

  /**
   * Instantiate a new {@code Remove} at the specified position that should hold the specified
   * {@code Stone}.
   *
   * @param horzPos the horizontal position.
   * @param vertPos the vertical position.
   * @param material the {@code GameMaterial}.
   */
  public Remove(int horzPos, int vertPos, GameMaterial material) {
    super(horzPos, vertPos, material);
  }

  /**
   * Apply this {@code Remove} to the specified {@code Board}. The specified {@code Board} is left
   * untouched and a modified copy of the {@code Board} is returned.
   *
   * @param board the {@code Board} to apply the move on.
   * @return board the {@code Board} with the {@code Move} applied to it.
   */
  public Board apply(Board board) throws AssertionError {
    if (board.get(getHorzPos(), getVertPos()) != getMaterial()) {
      throw new AssertionError("expected Stone not found at specified remove position");
    }
    Board newBoard = new Board(board);
    newBoard.put(getHorzPos(), getVertPos(), BoardFeature.EMPTY);
    return newBoard;
  }
}
