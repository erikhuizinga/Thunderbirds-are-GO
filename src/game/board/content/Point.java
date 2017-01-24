package game.board.content;

/**
 * Created by erik.huizinga on 23-1-17.
 */
public class Point extends BoardContent {

  private final int index;
  private final BoardFeatures content;

  protected Point(int index, BoardFeatures content) {
    this.index = index;
    this.content = content;
  }

  public int getIndex() {
    return index;
  }
}
