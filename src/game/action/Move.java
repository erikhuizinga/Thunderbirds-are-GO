package game.action;

import game.material.board.Board;
import game.material.Stone;

/** A move by a player. Created by erik.huizinga on 24-1-17. */
public class Move {

  private final Stone stone;
  private final int x;
  private final int y;

  /**
   * Instantiates a new {@code Move} at the specified position and the
   *
   * @param x the x position
   * @param y the y
   * @param stone the stone
   */
  public Move(int x, int y, Stone stone) {
    this.x = x;
    this.y = y;
    this.stone = stone;
  }

  /**
   * Apply this {@code Move} to the specified {@code Board}. The specified {@code Board} is left
   * untouched and a modified copy of the {@code Board} is returned.
   *
   * @param board the {@code Board} to apply the move on.
   * @return board the {@code Board} with the {@code Move} applied to it.
   */
  public Board apply(Board board) {
    Board newBoard = new Board(board);
    newBoard.put(x, y, stone);
    return newBoard;
  }
}
