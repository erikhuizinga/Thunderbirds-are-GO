package game.material.board;

import game.material.Material;
import game.material.PositionedMaterial;
import game.material.PositionedStone;
import game.material.Stone;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** A Go board. Created by erik.huizinga on 23-1-17. */
public class Board {

  /** The single-side dimension of the playable square grid. */
  private final int dim;

  /** The grid in which the board's material is stored. */
  private Grid grid;

  /**
   * Construct a Go board with the specified single-side dimension of the playable grid.
   *
   * @param dim the dimension.
   */
  public Board(int dim) {
    this.dim = dim;
    setGrid(new Grid(dim));
  }

  /**
   * Instantiate a new {@code Board} as a copy of another.
   *
   * @param board the board to copy.
   */
  public Board(Board board) {
    dim = board.getDim();
    setGrid(new Grid(board.getGrid()));
  }

  /** @return the grid */
  private Grid getGrid() {
    return grid;
  }

  /** @param grid the grid */
  private void setGrid(Grid grid) {
    this.grid = grid;
  }

  /** @return the single-side dimension of the playable square grid. */
  public int getDim() {
    return dim;
  }

  /** Put the specified {@code PositionedMaterial} on the {@code Board}. */
  public void put(PositionedMaterial positionedMaterial) {
    getGrid()
        .put(
            getGrid()
                .playable2Ind(
                    Arrays.asList(
                        positionedMaterial.getPlayableX(), positionedMaterial.getPlayableY())),
            positionedMaterial.getMaterial());
  }

  /**
   * Get the {@code Material} on the {@code Board} at the specified playable indices.
   *
   * @param x the horizontal playable index.
   * @param y the vertical playable index.
   * @return the {@code Material}.
   */
  public Material get(int x, int y) {
    return get(getGrid().playable2Ind(Arrays.asList(x, y)));
  }

  /**
   * Get the {@code Material} on the {@code Board} at the specified full grid linear index.
   *
   * @param index the index.
   * @return the {@code Material}.
   */
  private Material get(int index) {
    return getGrid().get(index);
  }

  /** @return the {@code Board} as a {@code String}. */
  @Override
  public String toString() {
    return getGrid().toString();
  }

  @Override
  public int hashCode() {
    return getGrid().hashCode();
  }

  /**
   * Get neighbouring {@code PositionedMaterial} of the specified {@code PositionedMaterial}.
   *
   * @param posM the {@code PositionedMaterial}.
   * @return the {@code List<PositionedMaterial>} of neighbours.
   */
  public List<PositionedMaterial> getNeighbors(PositionedMaterial posM) {
    List<PositionedMaterial> neighbors = new ArrayList<>();
    Grid grid = getGrid();
    int index = grid.playable2Ind(Arrays.asList(posM.getPlayableX(), posM.getPlayableY()));
    List<Integer> neighborIndices = grid.getNeighborsMap().get(index);
    List<Integer> neighborPlayableIndex;
    PositionedMaterial neighbor = null;
    Material neighborMaterial;
    for (int neighborIndex : neighborIndices) {
      neighborPlayableIndex = grid.ind2Playable(neighborIndex);

      neighborMaterial = grid.get(neighborIndex);
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
