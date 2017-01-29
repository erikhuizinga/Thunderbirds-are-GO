package game.material;

/** Created by erik.huizinga on 29-1-17. */
public abstract class PositionedMaterial implements Material {

  /** The horizontal position on the playable grid. */
  private final int playableX;

  /** The vertical position on the platable grid. */
  private final int playableY;

  /** The {@code Material}. */
  private final Material material;

  /**
   * Instantiate a new {@code PositionedMaterial}, which is {@code Material} at the specified
   * playable position on a board.
   *
   * @param playableX the horizontal position on the playable grid.
   * @param playableY the vertical position on the playable grid.
   * @param material the {@code Material}.
   */
  public PositionedMaterial(int playableX, int playableY, Material material) {
    this.playableX = playableX;
    this.playableY = playableY;
    this.material = material;
  }

  @Override
  public boolean isPlayable() {
    return getMaterial().isPlayable();
  }

  /** @return the horizontal position on the playable grid. */
  public int getPlayableX() {
    return playableX;
  }

  /** @return the vertical position on the playable grid. */
  public int getPlayableY() {
    return playableY;
  }

  /** @return the {@code Material}. */
  public Material getMaterial() {
    return material;
  }
}
