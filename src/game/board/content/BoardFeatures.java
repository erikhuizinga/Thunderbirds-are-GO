package game.board.content;

/**
 * Created by erik.huizinga on 24-1-17.
 */
public enum BoardFeatures {
  /**
   * An empty point, i.e., a {@code Point} without a {@code Stone} on the {@code Grid}.
   */
  EMPTY,

  /**
   * A board side, i.e., a point one location unit outside the playable {@code Grid} where no
   * {@Stone} may be played.
   */
  SIDE;

  /**
   * Returns the name of this enum constant, as contained in the
   * declaration.  This method may be overridden, though it typically
   * isn't necessary or desirable.  An enum type should override this
   * method when a more "programmer-friendly" string form exists.
   *
   * @return the name of this enum constant
   */
  @Override
  public String toString() {
    String string;
    switch (this) {
      case EMPTY:
        string = ".";
        break;
      case SIDE:
        string = " ";
        break;
      default:
        string = null;
    }
    return string;
  }
}
