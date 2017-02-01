package players.strategy;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;

/** The interface for the {@code Strategy} of the {@code ComputerPlayer}. */
public interface Strategy {

  /** @return the name of the {@code Strategy}. */
  String getName();

  /** @return the next {@code Move} as determined by this {@code Strategy}. */
  Move nextMove(Board board, Stone stone);
}
