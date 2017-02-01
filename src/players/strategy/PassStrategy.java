package players.strategy;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;

/** Created by erik.huizinga on 1-2-17. */
public class PassStrategy implements Strategy {

  @Override
  public String getName() {
    return "pass";
  }

  @Override
  public Move nextMove(Board board, Stone stone) {
    return null;
  }
}
