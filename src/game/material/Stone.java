package game.material;

/** Created by erik.huizinga on 23-1-17. */
public enum Stone implements Material {

  /** The black stone. */
  BLACK,

  /** The white stone. */
  WHITE;

  /** @return the {@code Stone} as a {@code String}. */
  @Override
  public String toString() {
    String string;
    switch (this) {
      case BLACK:
        string = "○"; // ○◯
        break;
      case WHITE:
        string = "●"; // ●⬤
        break;
      default:
        string = null;
    }
    return string;
  }

  /** @return the other {@code Stone}. */
  public Stone other() {
    Stone stone;
    switch (this) {
      case BLACK:
        stone = WHITE;
        break;
      case WHITE:
        stone = BLACK;
        break;
      default:
        stone = null;
    }
    return stone;
  }
}
