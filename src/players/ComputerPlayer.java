package players;

import game.action.Move;
import game.material.Stone;
import java.util.Arrays;
import java.util.List;
import players.strategy.RandomStrategy;
import players.strategy.Strategy;

/** Created by erik.huizinga on 24-1-17. */
public class ComputerPlayer extends Player {

  public static final List<String> NAMES =
      Arrays.asList(
          "Willy Wonka",
          "Oompa Loompa",
          "Fluffy",
          "B100",
          "HAL 9000",
          "AlphaGo",
          "Watson",
          "Dragon",
          "Sloth",
          "Proteus IV",
          "I.N.T.E.L.L.I.G.E.N.C.E.",
          "Funnybot",
          "KITT",
          "A.I.D.A.",
          "E.R.I.K.",
          "N.I.A.L.A.");

  /** The type of {@code Player}. */
  public static final String REALIZATION = "computer";

  /** The {@code Strategy} of the {@code ComputerPlayer}. */
  private Strategy strategy;

  /**
   * Instantiate a {@code ComputerPlayer} with a random strategy.
   *
   * @param stone the {@code Stone} of the {@code ComputerPlayer}.
   */
  public ComputerPlayer(Stone stone) {
    super(util.ListTools.random(NAMES), stone);
    strategy = new RandomStrategy();
  }

  public ComputerPlayer(String name, Stone stone) {
    super(name, stone);
    strategy = new RandomStrategy();
  }

  /** @return a randomly generated computer player name. */
  public static String randomName() {
    return randomName(NAMES);
  }

  @Override
  public Move nextMove() {
    return null;
  }

  public static String displayFormat(String name, Stone stone) {
    return Player.displayFormat(name, stone) + ", " + REALIZATION;
  }
}
