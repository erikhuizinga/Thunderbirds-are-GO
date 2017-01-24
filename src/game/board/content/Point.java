package game.board.content;

/**
 * Features of the actual board, i.e., empty spaces and outer sides of the grid.
 *
 * Created by erik.huizinga on 23-1-17.
 */

public class Point extends Content {

  /**
   * An empty point, i.e., a {@code Point} without a {@code Stone} on the {@code Grid}.
   */
  public static final String EMPTY = "·";

  /**
   * A board side, i.e., a point one location unit outside the playable {@code Grid} where no
   * {@Stone} may be played.
   */
  public static final String SIDE = " ";

  public Point(String content) {
    super(content);
  }
}
