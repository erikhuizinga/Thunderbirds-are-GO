package game;

import game.action.Move;
import game.material.PositionedMaterial;
import game.material.PositionedStone;
import game.material.Stone;
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
   * Handle dynamical validity of the specified {@code Board} after playing {@code
   * PositionedMaterial}.
   *
   * @param board
   * @param positionedMaterial
   */
  static void handleDynamicalValidity(Board board, PositionedMaterial positionedMaterial) {
    DynamicalValidator validator = new DynamicalValidator(board);
    validator.validate(positionedMaterial);
    validator.enforce();
  }

  /**
   * Determine if the specified {@code Board} does not yet exists in the specified {@code Go} game's
   * board history.
   *
   * @param go the {@code Go} game.
   * @param board the {@code Board}.
   * @return {@code true} if the {@code Move} is historically valid; {@code false} otherwise.
   */
  public static boolean isHistoricallyValid(Go go, Board board) {
    return !go.getBoardHistory().contains(board);
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
    private final int first = 1;
    private final int doing = 2;
    private final int depends = 3;
    private final int done = 4;
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

    void validate(PositionedMaterial posM) {
      List<Integer> playable = posM.getPlayablePosition();
      int ind = board.playable2Ind(playable);
      List<Integer> neighborIndices = board.getNeighborsMap().get(ind);
      List<PositionedMaterial> neighborMaterials = board.getNeighbors(posM);

      // Store a map of neighbouring indices to neighbouring material
      Map<Integer, PositionedMaterial> neighborInd2MatMap = new HashMap<>();
      for (int i = 0; i < neighborIndices.size(); i++) {
        neighborInd2MatMap.put(neighborIndices.get(i), neighborMaterials.get(i));
      }

      // Validate
      do {
        switchLabel:
        switch (status.get(ind)) {
          case first:
            for (int neighborIndex : neighborIndices) {
              // Determine if the neighbour is to be validated
              if (status.get(neighborIndex) < done) {
                // Validate the neighbour
                List<Integer> neighborPlayableIndex = board.ind2Playable(neighborIndex);
                PositionedMaterial neighbor =
                    new PositionedStone(
                        neighborPlayableIndex.get(0),
                        neighborPlayableIndex.get(1),
                        (Stone) board.get(neighborIndex));
                validate(neighbor);
              }
            }

            // Set the first stone's status to be validated
            status.put(ind, doing);
            break;

          case doing:
            // Check neighbours until a liberty (empty neighbour) is found
            for (Entry<Integer, PositionedMaterial> entry : neighborInd2MatMap.entrySet()) {
              if (entry.getValue().containsMaterial(Feature.EMPTY) || !valid.get(entry.getKey())) {
                status.put(ind, done);
                break switchLabel;
              }
            }

            // Check neighbours for stones of the same colour
            for (PositionedMaterial neighborMaterial : neighborMaterials) {
              if (neighborMaterial.containsMaterial(posM.getMaterial())) {
                status.put(ind, depends);
                break switchLabel;
              }
            }

            /* Set this stone to be invalid as it has no liberties, because all its neighbours are
             * either sides of the board or opponent stones
             */
            status.put(ind, done);
            valid.put(ind, false);
            break;

          case depends:
            // Determine if all neighbours have been checked already
            for (Entry<Integer, PositionedMaterial> entry : neighborInd2MatMap.entrySet()) {
              if (status.get(entry.getKey()) <= doing) {
                validate(entry.getValue());
                break;
              }
            }

            break;

          case done:
            // Do nothing, continue to return
            break;

          default: // Initially, status.get(.) returns 0, which is the default
            // Set this stone's status to be the first
            status.put(ind, first);
            // Set neighbour status to doing
            for (int neighborIndex : neighborIndices) {
              if (status.get(neighborIndex) == todo) {
                status.put(neighborIndex, doing);
              }
            }
        }
      } while (status.get(ind) < done);
    }

    void enforce() {
      if (valid.containsValue(false)) {
        for (Entry<Integer, Boolean> entry : valid.entrySet()) {
          if (!entry.getValue()) {
            board.put(entry.getKey(), Feature.EMPTY);
          }
        }
      }
    }
  }
}
