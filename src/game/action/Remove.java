package game.action;

import game.material.BoardFeature;
import game.material.Material;
import game.material.PositionedMaterial;
import game.material.board.Board;

/** Created by erik.huizinga on 29-1-17. */
public class Remove extends Move {

  /**
   * Instantiate a new {@code Remove} at the specified position. The position should hold the
   * specified {@code Stone} before the {@code Remove} is applied.
   *
   * @param playableX the horizontal position on the playable grid.
   * @param playableY the vertical position on the playable grid.
   * @param material the {@code Material}.
   */
  public Remove(int playableX, int playableY, Material material) {
    super(playableX, playableY, material);
  }

  public Remove(PositionedMaterial positionedMaterial) {
    super(positionedMaterial);
  }

  /**
   * Apply this {@code Remove} to the specified {@code Board}. This requires the specified {@code
   * Material} in the constructor of {@code Remove} to be currently located at the specified
   * position. If this is not the case, a new {@code AssertionError} is thrown. If the {@code
   * Remove} is applied successfully, the specified {@code Board} is left untouched and a modified
   * copy of the {@code Board} is returned.
   *
   * @param board the {@code Board} to apply the move on.
   * @return board the {@code Board} with the {@code Move} applied to it.
   * @throws AssertionError if the expected {@code Material} is not found at the specified position.
   */
  public Board apply(Board board) throws AssertionError {
    if (board.get(getPlayableX(), getPlayableY()) != getMaterial()) {
      throw new AssertionError("expected material not found at specified remove position");
    }
    Board newBoard = new Board(board);
    newBoard.put(getPlayableX(), getPlayableY(), BoardFeature.EMPTY);
    return newBoard;
  }
}
