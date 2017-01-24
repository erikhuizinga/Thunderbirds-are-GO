package game.board.content;

import java.util.Map.Entry;

/**
 * Created by erik.huizinga on 24-1-17.
 */
public class PlayableGrid extends Grid {

  public PlayableGrid(int boardSideLength) {
    super(boardSideLength);
  }

  /**
   * Get the linear index on the grid from the subscript indices.
   *
   * @param sub The subscript indices.
   * @return The linear index.
   */
  @Override
  public int sub2Ind(int[] sub) {
    return super.sub2Ind(sub);
  }
}
