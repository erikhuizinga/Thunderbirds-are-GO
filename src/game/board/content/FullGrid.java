package game.board.content;

/**
 * Created by erik.huizinga on 24-1-17.
 */
public class FullGrid extends Grid {

  public FullGrid(int boardSideLength) {
    super(boardSideLength + 2);
  }

  /**
   * Initialise the {@code Grid} by setting all values to {@code Point.EMPTY} and initialise
   * the {@code sub2Ind} and {@code ind2Sub} methods.
   */
  @Override
  protected void init() {
    super.init();
  }
}