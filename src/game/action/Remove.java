package game.action;

import game.material.Material;
import game.material.PositionedMaterial;
import game.material.board.Board;
import game.material.board.Feature;
import game.material.board.PositionedFeature;

/** Created by erik.huizinga on 29-1-17. */
public class Remove extends Move {

  private final PositionedFeature empty;

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
    empty = new PositionedFeature(playableX, playableY, Feature.EMPTY);
  }

  /**
   * Instantiate a new {@code Remove}, which is a {@code PositionedMaterial} that can be applied to
   * a {@code Board}. The position should hold the specified {@code Stone} before the {@code Remove}
   * is applied.
   *
   * @param positionedMaterial the {@code PositionedMaterial}.
   */
  public Remove(PositionedMaterial positionedMaterial) {
    super(positionedMaterial);
    empty =
        new PositionedFeature(
            positionedMaterial.getPlayableX(), positionedMaterial.getPlayableY(), Feature.EMPTY);
  }

  public PositionedFeature getEmpty() {
    return empty;
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
    newBoard.put(getEmpty());
    return newBoard;
  }
}
