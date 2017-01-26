package game.material;

/** Created by erik.huizinga on 25-1-17. */
public interface GameMaterial {

  /**
   * @return {@code true} if a {@code Stone} can be played on top of this {@code GameMaterial};
   *     {@code false} otherwise.
   */
  default boolean isPlayable() {
    return false;
  }
}
