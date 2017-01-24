package game.board.content;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A grid on a Go game board.
 * Created by erik.huizinga on 23-1-17.
 */
public class Grid {

  /**
   * The playable {@code Board} grid, i.e., the points that may be {@code BoardFeatures.EMPTY} or
   * hold a {@code Stone}.
   */
  private final Map<Integer, BoardContent> playableGrid = new HashMap<>();

  /**
   * The full {@code Board} grid, i.e., the playable grid surrounded by one layer of {@code
   * BoardFeatures.SIDE} points.
   */
  private final Map<Integer, BoardContent> fullGrid = new HashMap<>();

  /**
   * The four neighbours of every {@code Point} on the playable grid.
   */
  private final Map<Integer, Set<Map<Integer, BoardContent>>> neighbors = new HashMap<>();

  public Grid(int boardSideLength) {
    initializeFullGrid(boardSideLength);
    initializePlayableGrid(boardSideLength);
    initializeNeighbors(boardSideLength);
  }

  /**
   * Initialise the full grid.
   *
   * @param boardSideLength The single-side board size.
   */
  private void initializeFullGrid(int boardSideLength) {
  }

  /**
   * Initialise the playable grid.
   *
   * @param boardSideLength The single-side board size.
   */
  private void initializePlayableGrid(int boardSideLength) {
  public void init(int dim) {
    int sub[] = new int[2];
    for (int ind = 0; ind < dim; ind++) {
      grid.put(ind, new Point(Point.EMPTY));
    }
  }

  /**
   * Initialise the neighbours of the playable grid.
   *
   * @param boardSideLength The single-side
   */
  private void initializeNeighbors(int boardSideLength) {
  }

  public Map<Integer, BoardContent> getFullGrid() {
    return fullGrid;
  }

  public Map<Integer, BoardContent> getPlayableGrid() {
    return playableGrid;
  }

  public Map<Integer, Set<Map<Integer, BoardContent>>> getNeighbors() {
    return neighbors;
  }
}
