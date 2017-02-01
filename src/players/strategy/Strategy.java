package players.strategy;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;

/** The interface for the {@code Strategy} of the {@code ComputerPlayer}. */
public interface Strategy {

  /** @return the name of the {@code Strategy}. */
  String getName();

  /**
   * Get the next {@code Move} with the specified {@code Stone} to play on the specified {@code
   * Board} as determined by this {@code Strategy}.
   *
   * @param board the {@code Board}.
   * @param stone the {@code Stone}.
   * @return the next {@code Move}.
   */
  Move nextMove(Board board, Stone stone);
}
