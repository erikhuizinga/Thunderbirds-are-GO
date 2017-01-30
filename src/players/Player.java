package players;

import game.action.Move;
import game.material.Stone;
import java.util.Arrays;
import java.util.List;

/** Created by erik.huizinga on 24-1-17. */
public abstract class Player {

  /** The type of {@code Player}. */
  public static final String REALIZATION = "player";

  public static final List<String> NAMES = Arrays.asList("player");

  private final String name;

  private final Stone stone;

  /**
   * Return the {@code Player} name.
   *
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Instantiate a {@code Player} with a random name and the specified {@code Stone}.
   *
   * @param stone the {@code Stone}.
   */
  public Player(Stone stone) {
    this(randomName(NAMES), stone);
  }

  /**
   * Instantiate as {@code Player} with the specified name and {@code Stone}.
   *
   * @param name the name.
   * @param stone the {@code Stone}.
   */
  public Player(String name, Stone stone) {
    this.name = name;
    this.stone = stone;
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

  /** @return the next {@code Move}. */
  public abstract Move nextMove();

  /** @return the stone the {@code Stone} the {@code Player} plays with. */
  Stone getStone() {
    return stone;
  }

  public static String displayFormat(String name, Stone stone) {
    return stone + " " + name;
  }

  /** @return the {@code Player} as a nice {@code String} for display. */
  @Override
  public String toString() {
    return displayFormat(getName(), getStone());
  }
}
