package game;

import game.action.Move;
import game.material.Stone;
import game.material.board.Board;

/**
 * A placed {@code Stone} is valid if:
 *
 * <ol>
 * <li>it is located on a location where the {@code Board} is {@code BoardFeature.EMPTY}, i.e.:
 *     <ol>
 *     <li>it is not located outside the {@code Board} playable grid, i.e., where the {@code Board}
 *         is {@code BoardFeature.SIDE},
 *     <li>it is not located on top of another {@code Stone},
 *     <li>it is not located outside the bounds of the full grid,
 *     </ol>
 *
 * <li>it commits suicide: it will immediately be removed from the board (along with any group it
 *     may have been part of,
 *     <ol>
 *     <li>if the suicide leads to the capture of a group of the opponent, the opponent group is
 *         removed from the board first, after which the suicide stone has liberties again and does
 *         not need to be removed from the board, as it will always have at least one liberty.
 *     </ol>
 *
 * <li>it does not reproduce any previous board layout ((super) Ko rule).
 * </ol>
 *
 * <p>Rule names:
 *
 * <dl>
 * <dt>Rule 1
 * <dd>technical validity
 * <dt>Rule 2
 * <dd>dynamical validity
 * <dt>Rule 3
 * <dd>is called historical validity
 * </dl>
 *
 * <p>Note that the rules are honoured in their numerical order when playing a move. Also note that
 * any technically valid move is also dynamically valid; if can be placed somewhere, it is valid.
 * However, when a legal move is played, the stone itself or adjacent stones may become dynamically
 * invalid; hence they are removed.
 *
 * <p>Created by erik.huizinga on 27-1-17.
 */
public abstract class Rules {

  /**
   * Determine if the specified {@code Move} is valid to play on the specified {@code Go} game.
   *
   * @param go the {@code Go} game.
   * @param move the {@code Move}.
   * @return {@code true} if the {@code Move} is valid; {@code false} otherwise.
   */
  public static boolean isValidMove(Go go, Move move) {

    Board board = go.getBoard();
    return isTechnicallyValid(board, move)
        // && isDynamicallyValid(board, move)
        && isHistoricallyValid(go, move);
  }

  /**
   * Determine technical validity of the specified {@code Move} on the specified {@code Board}.
   *
   * @param board the {@code Board}.
   * @param move the {@code Move}.
   * @return {@code true} if the {@code Move} is technically valid; {@code false} otherwise.
   */
  private static boolean isTechnicallyValid(Board board, Move move) {
    boolean isValid;
    try {
      isValid = board.get(move.getHorzPos(), move.getVertPos()).isPlayable();
    } catch (AssertionError e) { // Thrown if the position is out of bounds
      isValid = false;
    }
    return isValid;
  }

  /**
   * Determine dynamical validity of the specified {@code Stone} on the specified playable location
   * on the specified {@code Board}.
   *
   * @param board the {@code Board}.
   * @param stone the {@code Stone}.
   * @param horzPos the horizontal position on the playable grid.
   * @param vertPos the vertical position on the playable grid.
   * @return {@code true} if the {@code Move} is dynamically valid; {@code false} otherwise.
   */
  private static boolean isDynamicallyValid(Board board, Stone stone, int horzPos, int vertPos) {
    boolean isValid = false;
    return isValid;
  }

  /**
   * Determine if the specified {@code Move} is historically valid in the specified {@code Go} game.
   *
   * @param go the {@code Go} game.
   * @param move the {@code Move}.
   * @return {@code true} if the {@code Move} is historically valid; {@code false} otherwise.
   */
  private static boolean isHistoricallyValid(Go go, Move move) {
    Board board = move.apply(go.getBoard());
    return !go.getBoardHistory().contains(board.hashCode());
  }

  /**
   * Determine the finished state of the specified {@code Go} game.
   *
   * @param go the {@code Go} game.
   * @return {@code true} if the game is finished; {@code false} otherwise.
   */
  public static boolean isFinished(Go go) {
    return false;
  }
}
