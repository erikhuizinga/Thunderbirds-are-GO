package game.action;

import game.board.content.Stone;

/**
 * A move by a player.
 * Created by erik.huizinga on 24-1-17.
 */
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
}
