package game;

import game.action.Move;
import game.material.PositionedMaterial;
import game.material.board.Board;
import game.material.board.Feature;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A placed {@code Stone} is valid if:
 *
 * <ol>
 * <li>it is located on a location where the {@code Board} is {@code Feature.EMPTY}, i.e.:
 *     <ol>
 *     <li>it is not located outside the {@code Board} playable grid, i.e., where the {@code Board}
 *         is {@code Feature.SIDE},
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
  public static boolean isTechnicallyValid(Board board, Move move) {
    boolean isValid;
    try {
      isValid = board.get(move.getPlayableX(), move.getPlayableY()).isPlayable();
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
   * @param posM the {@code PositionedMaterial}.
   * @param validator the {@code DynamicalValidator}.
   * @return {@code true} if dynamically valid; {@code false otherwise}.
   */
  private static boolean isDynamicallyValid(
      Board board, PositionedMaterial posM, DynamicalValidator validator) {
    //    if () { // TODO 1: vind lineaire index van posM a.d.h.v. grid. 2: return validator.getValid().get(index)
    //
    //    }
    return false;
  }

  /**
   * Determine if the specified {@code Move} is historically valid in the specified {@code Go} game.
   *
   * @param go the {@code Go} game.
   * @param move the {@code Move}.
   * @return {@code true} if the {@code Move} is historically valid; {@code false} otherwise.
   */
  public static boolean isHistoricallyValid(Go go, Move move) {
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

  private static void setStatus(int status, int newStatus, List<Integer> illegal) {}

  public static class DynamicalValidator {

    /**
     * The status map contains the status of every board element mapped to its linear index. Status
     * means the following:
     *
     * <dl>
     * <dt>0: to do
     * <dd>not yet validated,
     * <dt>1: doing
     * <dd>being validated,
     * <dt>2: depends
     * <dd>validity depends on neighbours,
     * <dt>3: done
     * <dd>done.
     * </dl>
     */
    private final Map<Integer, Integer> status = new HashMap<>();

    private final int todo = 0;
    private final int doing = 1;
    private final int depends = 2;
    private final int done = 3;

    private final Map<Integer, Boolean> valid = new HashMap<>();
    private final Board board;

    public DynamicalValidator(Board board) {
      this.board = board;
      for (int i = 0; i < (board.getDim() + 2) * (board.getDim() + 2); i++) {
        valid.put(i, true);
        if (board.get(i) == Feature.SIDE || board.get(i) == Feature.EMPTY) {
          // Set either the sides, or empty fields to done; they are always valid
          status.put(i, done);
        } else { // The board has a stone at this index
          status.put(i, todo);
        }
      }
    }

    public Board getBoard() {
      return board;
    }

    DynamicalValidator validate(PositionedMaterial posM) {
      List<Integer> playable = posM.getPlayablePosition();
      int ind = getBoard().playable2Ind(playable);
      List<Integer> neighborIndices = getBoard().getNeighborsMap().get(ind);
      List<PositionedMaterial> neighboringMaterial = board.getNeighbors(posM); //TODO maak return type van getNeighbors(posM) een List<Material>

      // Store a map of neighbouring indices to neighbouring material
      Map<Integer, PositionedMaterial> neighIndexMaterialMap = new HashMap<>();
      for (int i = 0; i < neighborIndices.size(); i++) {
        neighIndexMaterialMap.put(neighborIndices.get(i), neighboringMaterial.get(i));
      }

      // Validate
      do {
        switch (status.get(ind)) {
          case doing:
            // Check all neighbours until a liberty (empty) is found
            for (PositionedMaterial neighMat : neighboringMaterial) {
              if (neighMat.containsMaterial(Feature.EMPTY)) {
                status.put(ind, done);
                break;
              }
            }

            // Check neighbours for stones of the same colour
            if (neighboringMaterial.contains(posM.getMaterial())) {
              status.put(ind, depends);
              break;
            }

            // Set this stone to be invalid as it has no liberties
            status.put(ind, done);
            valid.put(ind, false);

            break;

          case depends:
            // Determine if all neighbours have been checked already
            for (Entry entry : neighIndexMaterialMap.entrySet()) {
                if (status.get(entry.getKey()) <= doing) {
                  validate((PositionedMaterial) entry.getValue()); //TODO cast must stay?
                  break;
                }
            }

            break;

          case done:
            // Do nothing, continue to return
            break;

          default: // Initially, status.get(.) returns 0, which is the default
            // Set this status to doing
            status.put(ind, doing);
            // Set neighbouring status to doing
            for (int neighborIndex : neighborIndices) {
              if (status.get(neighborIndex) == todo) {
                status.put(neighborIndex, doing);
              }
            }
        }
      } while (status.get(ind) <= done);
      return this;
    }
  }
}
