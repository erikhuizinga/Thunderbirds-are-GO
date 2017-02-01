package game;

import game.action.Move;
import game.action.Move.MoveType;
import game.material.Material;
import game.material.PositionedMaterial;
import game.material.Stone;
import game.material.board.Board;
import game.material.board.Feature;
import java.util.HashMap;
import java.util.LinkedList;
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
    if (!isValid) {
      System.out.println("It is not allowed to play there.");
    }
    return isValid;
  }

  /**
   * Play the specified {@code Move} on the specified {@code Board} and ensure dynamical validity of
   * the {@code Board}. The specified {@code Board} is left untouched and a new {@code Board} is
   * returned.
   *
   * @param board the {@code Board} to play on.
   * @param move the {@code Move} to play.
   * @return the dynamically valid {@code Board} with the {@code Move} played on it.
   */
  static Board playWithDynamicalValidation(Board board, Move move) {
    Board nextBoard = move.apply(board);
    handleDynamicalValidity(nextBoard, move);
    return nextBoard;
  }

  /**
   * Handle dynamical validity of the specified {@code Board} after playing {@code
   * PositionedMaterial}.
   *
   * @param board the {@code Board} to validate.
   * @param positionedMaterial the {@code PositionedMaterial} that may have dynamically invalidated
   *     the {@code Board}.
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
    if (!go.getBoardHistory().contains(board.hashCode())) {
      return true;
    } else {
      System.out.println("This move would violate the super ko rule.");
      return false;
    }
  }

  /**
   * Determine the finished state of the specified {@code Go} game.
   *
   * @param go the {@code Go} game.
   * @return {@code true} if the game is finished; {@code false} otherwise.
   */
  public static boolean isFinished(Go go) {
    if (isFinishedAfterPasses(go)) {
      return true;
    }
    if (isFinishedAfterTableflip(go)) {
      return true;
    }
    return false;
  }

  /**
   * Determine if the specified {@code Go} game is finished due to a pass by the white player after
   * a pass by the black player.
   *
   * @param go the {@code Go} game.
   * @return {@code true} if the game is finished; {@code false} otherwise.
   */
  public static boolean isFinishedAfterPasses(Go go) {
    return go.getCurrentPlayer().getStone() == Stone.WHITE
        && go.getWhitePlayer().getMoveType() == MoveType.PASS
        && go.getBlackPlayer().getMoveType() == MoveType.PASS;
  }

  /**
   * Determine if the specified {@code Go} game is finished due to a tableflip.
   *
   * @param go the {@code Go} game.
   * @return {@code true} if the game is finished; {@code false} otherwise.
   */
  public static boolean isFinishedAfterTableflip(Go go) {
    return go.getCurrentPlayer().getMoveType() == MoveType.TABLEFLIP;
  }

  /** The dynamical validator class to dynamically validate boards with. */
  public static class DynamicalValidator {

    /**
     * The status map contains the status of every board element mapped to its linear index. Status
     * means the following:
     *
     * <dl>
     * <dt>0: to do
     * <dd>not yet validated,
     * <dt>1: first
     * <dd>the first stone starting validation,
     * <dt>2: doing
     * <dd>being validated,
     * <dt>3: depends
     * <dd>validity depends on neighbours,
     * <dt>4: done
     * <dd>done.
     * </dl>
     */
    private final Map<Integer, Integer> status = new HashMap<>();

    /**
     * The {@code Map} of boolean flags, indicating dynamical validity of every position on the
     * board's full grid, indexed by linear indices.
     */
    private final Map<Integer, Boolean> valid = new HashMap<>();

    private final int todo = 0;
    private final int first = 1;
    private final int doing = 2;
    private final int depends = 3;
    private final int done = 4;

    /**
     * A dynamically growing {@code Map} of neighbouring {@code PositionedMaterial} indexed by full
     * grid linear indices. This map is used to quickly find neighbours and their positions during
     * the validation algorithm.
     */
    private final Map<Integer, PositionedMaterial> neighborIndex2PositionedMaterialMap =
        new HashMap<>();

    /** The {@code Board} being dynamically validated. */
    private final Board board;

    private int firstIndex;

    /**
     * Instantiate a new {@code DynamicalValidator} of the specified {@code Board}.
     *
     * @param board the {@code Board}.
     */
    DynamicalValidator(Board board) {
      this.board = board;
      for (int i = 0; i < (board.getDim() + 2) * (board.getDim() + 2); i++) {
        valid.put(i, true);
        if (board.get(i) instanceof Feature) {
          // Set either the board features to done; they are always valid
          status.put(i, done);
        } else { // The board has a stone at this index
          status.put(i, todo);
        }
      }
    }

    /**
     * Validate the {@code Board} starting from the specified {@code PositionedMaterial}.
     *
     * @param positionedMaterial the {@code PositionedMaterial}.
     */
    void validate(PositionedMaterial positionedMaterial) {
      List<Integer> playable = positionedMaterial.getPlayablePosition();
      int index = board.playable2Ind(playable);

      // Get neighbour indices and materials
      List<Integer> neighborIndices = new LinkedList<>(board.getNeighborsMap().get(index));
      List<PositionedMaterial> neighborMaterials =
          new LinkedList<>(board.getNeighbors(positionedMaterial));

      // Remove opponent neighbours if their status is done; this saves iterations later in the
      // algorithm
      for (int i = neighborIndices.size() - 1; i >= 0; i--) {
        Material material = neighborMaterials.get(i).getMaterial();
        if (material != Feature.EMPTY
            && material != positionedMaterial.getMaterial()
            && status.get(neighborIndices.get(i)) == done) {
          neighborIndices.remove(i);
          neighborMaterials.remove(i);
        }
      }

      // Add to the map of neighbouring indices to neighbouring positioned material
      for (int i = 0; i < neighborIndices.size(); i++) {
        if (!neighborIndex2PositionedMaterialMap.containsKey(neighborIndices.get(i))) {
          neighborIndex2PositionedMaterialMap.put(neighborIndices.get(i), neighborMaterials.get(i));
        }
      }

      // Validate
      do {
        switchLabel:
        switch (status.get(index)) {
          case first:
            for (int neighborIndex : neighborIndices) {
              // Determine if the neighbour is to be validated
              if (status.get(neighborIndex) < done) {
                validate(neighborIndex2PositionedMaterialMap.get(neighborIndex));
              }
            }
            if (status.get(index) < done) {
              // Set the first stone's status to be validated
              status.put(index, doing);
            }
            break;

          case doing:
            // Check neighbours until a liberty (empty neighbour) is found
            for (int neighborIndex : neighborIndices) {
              if (neighborIndex2PositionedMaterialMap
                      .get(neighborIndex)
                      .containsMaterial(Feature.EMPTY)
                  || !valid.get(neighborIndex)) {
                status.put(index, done);
                break switchLabel;
              }
            }

            // Check neighbours for stones of the same colour
            for (PositionedMaterial neighborMaterial : neighborMaterials) {
              if (neighborMaterial.containsMaterial(positionedMaterial.getMaterial())) {
                status.put(index, depends);
                break switchLabel;
              }
            }

            /* Set this stone to be invalid as it has no liberties, because all its neighbours are
             * either sides of the board or opponent stones
             */
            status.put(index, done);
            valid.put(index, false);
            break;

          case depends:
            // Determine if all neighbours have been checked already
            for (int neighborIndex : neighborIndices) {
              if (status.get(neighborIndex) <= doing // Not yet done and...
                  && neighborIndex != firstIndex) /* ...not the first stone */ {
                if (status.get(neighborIndex) == todo) {
                  // Ensure first status is not reused in a recursive call to this method
                  status.put(neighborIndex, doing);
                }
                validate(neighborIndex2PositionedMaterialMap.get(neighborIndex));
                break switchLabel;
              }

              // Keep this stone as valid if a neighbouring stone is ...
              if (valid.get(neighborIndex) // ... valid and...
                  && status.get(neighborIndex) == done // ... done and ...
                  && neighborIndex2PositionedMaterialMap.get(neighborIndex).getMaterial()
                      == positionedMaterial.getMaterial()) /* ... of the same colour */ {
                status.put(index, done);
                break switchLabel;
              }
            }
            /*
            At this point all neighbours have been done, except maybe the first, so check this
            and validate the first stone before this one if it's of the same colour
            */
            if (neighborIndex2PositionedMaterialMap.get(firstIndex).getMaterial()
                == positionedMaterial.getMaterial()) {
              // Validate the first before this one
              status.put(firstIndex, doing);
              validate(neighborIndex2PositionedMaterialMap.get(firstIndex));
              if (valid.get(firstIndex)) {
                status.put(index, done);
              }
              break;
            }
            status.put(index, done);
            valid.put(index, false);
            break;

          case done:
            // Do nothing, continue to return
            break;

          default: // Initially, status.get(.) returns 0, which is the default
            // Set this stone's status to be the first
            status.put(index, first);
            firstIndex = index;
            // Set neighbour status to doing
            for (int neighborIndex : neighborIndices) {
              if (status.get(neighborIndex) == todo) {
                status.put(neighborIndex, doing);
              }
            }
        }
      } while (status.get(index) < done);
    }

    /**
     * Enforce dynamical validity on the {@code Board} by removing every dynamically invalid {@code
     * Stone}.
     */
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
