package game.material.board;

import game.material.Material;
import game.material.PositionedMaterial;
import game.material.PositionedStone;
import game.material.Stone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A Go board. Created by erik.huizinga on 23-1-17. */
public class Board extends Grid {

  /**
   * Instantiate a new Go {@code Board} with the specified single-side dimension of the playable
   * {@code Grid}.
   *
   * @param dim the dimension.
   */
  public Board(int dim) {
    super(dim);
  }

  /**
   * Instantiate a new {@code Board} as a copy of another.
   *
   * @param board the {@code Board} to copy.
   */
  public Board(Board board) {
    super(board);
  }

  /** Put the specified {@code PositionedMaterial} on the {@code Board}. */
  public void put(PositionedMaterial positionedMaterial) {
    getGrid()
        .put(
            playable2Ind(
                Arrays.asList(
                    positionedMaterial.getPlayableX(), positionedMaterial.getPlayableY())),
            positionedMaterial.getMaterial());
  }

  /**
   * Get the {@code Material} on the {@code Board} at the specified playable indices.
   *
   * @param playableX the horizontal playable index.
   * @param playableY the vertical playable index.
   * @return the {@code Material}.
   */
  public Material get(int playableX, int playableY) {
    return get(playable2Ind(Arrays.asList(playableX, playableY)));
  }

  /**
   * Get neighbouring {@code PositionedMaterial} of the specified {@code PositionedMaterial}.
   *
   * @param positionedMaterial the {@code PositionedMaterial}.
   * @return the {@code List<PositionedMaterial>} of neighbours.
   */
  public List<PositionedMaterial> getNeighbors(PositionedMaterial positionedMaterial) {
    List<PositionedMaterial> neighbors = new ArrayList<>();
    int index =
        playable2Ind(
            Arrays.asList(positionedMaterial.getPlayableX(), positionedMaterial.getPlayableY()));
    List<Integer> neighborIndices = getNeighborsMap().get(index);
    List<Integer> neighborPlayableIndex;
    PositionedMaterial neighbor = null;
    Material neighborMaterial;
    for (int neighborIndex : neighborIndices) {
      neighborPlayableIndex = ind2Playable(neighborIndex);

      neighborMaterial = get(neighborIndex);
      if (neighborMaterial instanceof Stone) {
        neighbor =
            new PositionedStone(
                neighborPlayableIndex.get(0),
                neighborPlayableIndex.get(1),
                (Stone) neighborMaterial);

      } else if (neighborMaterial instanceof Feature) {
        neighbor =
            new PositionedFeature(
                neighborPlayableIndex.get(0),
                neighborPlayableIndex.get(1),
                (Feature) neighborMaterial);
      }
      neighbors.add(neighbor);
    }
    return neighbors;
  }
}
