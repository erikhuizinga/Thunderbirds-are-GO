package game.board.content;

/**
 * Created by erik.huizinga on 23-1-17.
 */
public abstract class Content {

  private final String content;

  /**
   * Construct the content of a location on the game board.
   *
   * @param content the content.
   */
  public Content(String content) {
    this.content = content;
  }

  /**
   * @return the content.
   */
  public String getContent() {
    return content;
  }

  /**
   * @return {@code true} if a {@code Stone} can be played on this {@code Content}; {@code false}
   * otherwise.
   */
  public boolean isPlayable() {
    return false;
  }
}
