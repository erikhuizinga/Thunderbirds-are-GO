package game.material.board;

import game.material.Material;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

  /**
   * Put the specified {@code Stone} on the {@code Board} at the specified playable grid indices.
   *
   * @param x the horizontal index of the playable grid.
   * @param y the vertical index of the playable grid.
   * @param material the {@code Material}.
   */
  public void put(int x, int y, Material material) {
    getGrid().put(getGrid().playable2Ind(Arrays.asList(x, y)), material);
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

  /**
   * Returns a hash code value for the object. This method is supported for the benefit of hash
   * tables such as those provided by {@link HashMap}.
   *
   * <p>The general contract of {@code hashCode} is:
   *
   * <ul>
   * <li>Whenever it is invoked on the same object more than once during an execution of a Java
   *     application, the {@code hashCode} method must consistently return the same integer,
   *     provided no information used in {@code equals} comparisons on the object is modified. This
   *     integer need not remain consistent from one execution of an application to another
   *     execution of the same application.
   * <li>If two objects are equal according to the {@code equals(Object)} method, then calling the
   *     {@code hashCode} method on each of the two objects must produce the same integer result.
   * <li>It is <em>not</em> required that if two objects are unequal according to the {@link
   *     Object#equals(Object)} method, then calling the {@code hashCode} method on each of the two
   *     objects must produce distinct integer results. However, the programmer should be aware that
   *     producing distinct integer results for unequal objects may improve the performance of hash
   *     tables.
   * </ul>
   *
   * <p>As much as is reasonably practical, the hashCode method defined by class {@code Object} does
   * return distinct integers for distinct objects. (This is typically implemented by converting the
   * internal address of the object into an integer, but this implementation technique is not
   * required by the Java&trade; programming language.)
   *
   * @return a hash code value for this object.
   * @see Object#equals(Object)
   * @see System#identityHashCode
   */
  @Override
  public int hashCode() {
    return getGrid().hashCode();
  }

//  public Map<Integer, List<Integer>> getNeighbors(int playableX, int playableY) {
//    return getGrid().getNeighbors(playableX, playableY);
//  }
}
