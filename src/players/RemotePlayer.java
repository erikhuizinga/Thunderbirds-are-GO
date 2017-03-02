package players;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;
import net.Peer;

/** Created by erik.huizinga on 2-3-17. */
public class RemotePlayer extends Player {

  private static String generalization = "remote";
  private final Peer peer;
  private Move nextMove = null;

  public RemotePlayer(Stone stone, Peer peer) {
    super(stone);
    this.peer = peer;
  }

  public RemotePlayer(Stone stone, String name, Peer peer) {
    super(stone, name);
    this.peer = peer;
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
    Move nextMove = this.nextMove;
    this.nextMove = null;
    return nextMove;
  }
}
