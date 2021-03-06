package game.material.board;

import game.material.Material;

/**
 * Points on the actual board, i.e., empty spaces and outer sides of the grid.
 *
 * <p>Created by erik.huizinga on 23-1-17.
 */
public enum Feature implements Material {

  /** An empty point, i.e., a {@code Feature} without a {@code Stone} on the {@code Grid}. */
  EMPTY,

  /**
   * A board side, i.e., a point one location unit outside the playable grid where no {@code Stone}
   * may be played.
   */
  SIDE;

  /**
   * @return {@code true} if a {@code Stone} can be played on top of this {@code Material}; {@code
   *     false} otherwise.
   */
  @Override
  public boolean isPlayable() {
    switch (this) {
      case EMPTY:
        return true;
      default:
        return false;
    }
  }

  /** @return the {@code Feature} as a {@code String}. */
  @Override
  public String toString() {
    String string;
    switch (this) {
      case EMPTY:
        string = "·";
        break;
      case SIDE:
        string = Grid.SPACE;
        break;
      default:
        string = null;
    }
    return string;
  }
}
