package game.action;

import game.material.Material;
import game.material.PositionedMaterial;
import game.material.board.Board;

/** A move by a player. Created by erik.huizinga on 24-1-17. */
public class Move extends PositionedMaterial {

  /**
   * Instantiate a new {@code Move}, which is a {@code PositionedMaterial} that can be applied to a
   * {@code Board}.
   *
   * @param playableX the horizontal position on the playable grid.
   * @param playableY the vertical position on the playable grid.
   * @param material the {@code Material}.
   */
  public Move(int playableX, int playableY, Material material) {
    super(playableX, playableY, material);
  }

  /**
   * Instantiate a new {@code Move}, which is a {@code PositionedMaterial} that can be applied to a
   * {@code Board}.
   *
   * @param positionedMaterial the {@code PositionedMaterial}.
   */
  public Move(PositionedMaterial positionedMaterial) {
    super(
        positionedMaterial.getPlayableX(),
        positionedMaterial.getPlayableY(),
        positionedMaterial.getMaterial());
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
    newBoard.put(this);
    return newBoard;
  }
}
