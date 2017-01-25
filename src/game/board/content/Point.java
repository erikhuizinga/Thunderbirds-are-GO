package game.board.content;

/**
 * Points on the actual board, i.e., empty spaces and outer sides of the grid.
 *
 * Created by erik.huizinga on 23-1-17.
 */

public class Point extends Content {

  /**
   * An empty point, i.e., a {@code Point} without a {@code Stone} on the {@code Grid}.
   */
  public static final String EMPTY = "·";

  /**
   * A board side, i.e., a point one location unit outside the playable grid where no {@code Stone}
   * may be played.
   */
  public static final String SIDE = " ";

  public Point(String content) {
    super(content);
  }

  @Override
  public boolean isPlayable() {
    switch (getContent()) {
      case EMPTY:
        return true;
      default:
        return false;
    }
  }
}
