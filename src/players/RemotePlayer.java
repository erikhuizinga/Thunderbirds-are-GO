package players;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;

/** Created by erik.huizinga on 2-3-17. */
public class RemotePlayer extends Player {

  private static String generalization = "remote";
  private Move nextMove = null;

  public RemotePlayer(Stone stone, String name) {
    super(stone, name);
  }

  @Override
  public String getGeneralization() {
    return generalization;
  }

  public void setNextMove(Move nextMove) {
    this.nextMove = nextMove;
  }

  @Override
  public Move nextMove(Board board) {
    while (nextMove == null) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException ignored) {
      }
    }
    Move nextMove = this.nextMove;
    this.nextMove = null;
    return nextMove;
  }
}
