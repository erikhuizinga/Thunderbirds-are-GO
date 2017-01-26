package game;

import game.material.board.Board;
import java.util.Observable;
import players.Player;

/**
 * A Go game with a board and two players. Created by erik.huizinga on 24-1-17.
 */
public class Go extends Observable implements Runnable {

  private final Board board;
  private final Player[] players;


  /**
   * Instantiates a game of Go with specified {@code Board} dimension and
   *
   * @param dim the single-side dimension of the {@code Board}.
   * @param p1 the first {@code Player}.
   * @param p2 the second {@code Player}.
   */
  Go(int dim, Player p1, Player p2) {
    board = new Board(dim);
    this.players = new Player[]{p1, p2};
  }

  @Override
  public void run() {
    //TODO
  }
}
