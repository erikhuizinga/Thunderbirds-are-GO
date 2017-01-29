package game;

import game.action.Move;
import game.material.BoardFeature;
import game.material.board.Board;

/** Created by erik.huizinga on 27-1-17. */
public abstract class Rules {

  /**
   * Determine if the specified {@code Move} is valid to play on the specified {@code Board}.
   *
   * @param go the {@code Go} game.
   * @param move the {@code Move}.
   * @return {@code true} if the {@code Move} is valid; {@code false} otherwise.
   */
  public static boolean isValidMove(Go go, Move move) {
    /* A move is valid if:
     *  1. it is located on a location where the {@code Board} is {@code BoardFeature.EMPTY}, i.e.:
     *   1.1 it is not located outside the {@code Board} playable grid, i.e., where the {@code
     *       Board} is {@code BoardFeature.SIDE},
     *   1.2 it is not located on top of another {@code Stone},
     *  2. it commits suicide: it will immediately be removed from the board (along with any group
     *     it may have been part of,
     *   2.1 if the suicide leads to the capture of a group of the opponent, the opponent group is
     *     removed from the board first, after which the suicide stone has liberties again and does
     *     not need to be removed from the board, as it will always have at least one liberty.
     *  3. it does not reproduce any previous board layout ((super) Ko rule)
     *
     * Rule 1 is called technical validity.
     * Rule 2 is called dynamical validity.
     * Rule 3 is called historical validity.
     *
     * Note that the rules are honoured in their numerical order when playing a move. Also note that
     * any technically valid move is also dynamically valid; if can be placed somewhere, it is
     * valid. However, when a legal move is played, the stone itself or adjacent stones may become
     * dynamically invalid; hence they are removed.
     * */

    Board board = go.getBoard();
    return isTechnicallyValid(board, move)
        // && isDynamicallyValid(board, move)
        && isHistoricallyValid(go, move);
  }

  public static boolean isTechnicallyValid(Board board, Move move) {
    boolean isValid;
    try {
      isValid = board.get(move.getHorzPos(), move.getVertPos()) == BoardFeature.EMPTY;
    } catch (AssertionError e) { // Thrown if the position is out of bounds
      isValid = false;
    }
    return isValid;
  }

  private static boolean isDynamicallyValid(Board board, Move move) {
    boolean isValid = false;
    return isValid;
  }

  private static boolean isHistoricallyValid(Go go, Move move) {
    Board board = move.apply(go.getBoard());
    return !go.getBoardHistory().contains(board.hashCode());
  }
}
