package game.material.board;

import game.material.PositionedMaterial;

/** Created by erik.huizinga on 29-1-17. */
public class PositionedFeature extends PositionedMaterial {
  public PositionedFeature(int playableX, int playableY, Feature feature) {
    super(playableX, playableY, feature);
  }
}
