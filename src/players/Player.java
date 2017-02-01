package players;

import game.action.Move;
import game.action.Move.MoveType;
import game.material.Stone;
import game.material.board.Board;
import java.util.Arrays;
import java.util.List;

/** Created by erik.huizinga on 24-1-17. */
public abstract class Player {

  /** The list of default {@code Player} names. */
  public static final List<String> NAMES = Arrays.asList("player");

  /** The {@code Player} name. */
  private final String name;

  /** The {@code Player} {@code Stone}. */
  private final Stone stone;

  /** The {@code MoveType} of the last {@code Move}. */
  private MoveType moveType;

  /**
   * Instantiate a {@code Player} with a random name and the specified {@code Stone}.
   *
   * @param stone the {@code Stone}.
   */
  public Player(Stone stone) {
    this(stone, randomName(NAMES));
  }

  /**
   * Instantiate as {@code Player} with the specified name and {@code Stone}.
   *
   * @param stone the {@code Stone}.
   * @param name the name.
   */
  public Player(Stone stone, String name) {
    this.stone = stone;
    this.name = name;
  }

  /**
   * Get a random name from the specified list of names.
   *
   * @param names the list of names to pick one randomly from.
   * @return a randomly picked name.
   */
  public static String randomName(List<String> names) {
    return util.ListTools.random(names);
  }

  /**
   * Get a display format for the specified player name and {@code Stone}.
   *
   * @param name the name.
   * @param stone the {@code Stone}.
   * @return the displayable format.
   */
  public static String displayFormat(String name, Stone stone) {
    return stone + " " + name;
  }

  /** @return the {@code Player} generalization. */
  public abstract String getGeneralization();

  /**
   * Get the last move by the {@code Player} as a {@code String}. Can be {@code "MOVE"} or {@code
   * "PASS"}.
   *
   * @return the last move.
   */
  public MoveType getMoveType() {
    return moveType;
  }

  /**
   * Set the current move type of the {@code Player}.
   *
   * @param moveType the {@code MoveType}.
   */
  public void setMoveType(MoveType moveType) {
    this.moveType = moveType;
  }

  /**
   * Return the {@code Player} name.
   *
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Get the next {@code Move} to play on the {@code Board}.
   *
   * @param board the {@code Board}.
   * @return the {@code Move}.
   */
  public abstract Move nextMove(Board board);

  /** @return the stone the {@code Stone} the {@code Player} plays with. */
  public Stone getStone() {
    return stone;
  }

  /** @return the {@code Player} as a nice {@code String} for display. */
  @Override
  public String toString() {
    return displayFormat(getName(), getStone());
  }
}
