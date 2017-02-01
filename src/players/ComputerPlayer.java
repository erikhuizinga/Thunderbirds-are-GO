package players;

import game.action.Move;
import game.action.Move.MoveType;
import game.material.Stone;
import game.material.board.Board;
import java.util.Arrays;
import java.util.List;
import players.strategy.RandomStrategy;
import players.strategy.Strategy;

/** Created by erik.huizinga on 24-1-17. */
public class ComputerPlayer extends Player {

  /** The list of default {@code ComputerPlayer} names. */
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
  private static final String generalization = "computer";

  /** The {@code Strategy} of the {@code ComputerPlayer}. */
  private Strategy strategy;

  /**
   * Instantiate a new {@code ComputerPlayer} with the specified {@code Stone} and the {@code
   * RandomStrategy} strategy.
   *
   * @param stone the {@code Stone} of the {@code ComputerPlayer}.
   */
  public ComputerPlayer(Stone stone) {
    super(stone, randomName());
    setStrategy(new RandomStrategy());
  }

  /**
   * Instantiate a new {@code ComputerPlayer} with the specified name, {@code Stone} and the {@code
   * RandomStrategy} strategy.
   *
   * @param stone the {@code Stone}.
   * @param name the name.
   */
  public ComputerPlayer(Stone stone, String name) {
    super(stone, name);
    setStrategy(new RandomStrategy());
  }

  /**
   * Instantiate a new {@code ComputerPlayer} with a random name and the specified {@code Stone} and
   * {@code Strategy}.
   *
   * @param stone the {@code Stone}.
   * @param strategy the {@code Stratevgy}.
   */
  public ComputerPlayer(Stone stone, Strategy strategy) {
    this(stone);
    setStrategy(strategy);
  }

  /**
   * Instatiate a new {@code ComputerPlayer} with the specified name, {@code Stone} and {@code
   * Strategy}.
   *
   * @param name the name.
   * @param stone the {@code Stone}.
   * @param strategy the {@code Strategy}.
   */
  public ComputerPlayer(String name, Stone stone, Strategy strategy) {
    this(stone, name);
    setStrategy(strategy);
  }

  /** @return a randomly generated computer player name. */
  public static String randomName() {
    return randomName(NAMES);
  }

  /**
   * Get a display format for the specified player name and {@code Stone}.
   *
   * @param name the name.
   * @param stone the {@code Stone}.
   * @return the displayable format.
   */
  public static String displayFormat(String name, Stone stone) {
    return Player.displayFormat(name, stone) + ", " + generalization;
  }

  @Override
  public String getGeneralization() {
    return generalization;
  }

  @Override
  public Move nextMove(Board board) {
    Move move = getStrategy().nextMove(board, getStone());
    if (move != null) {
      setMoveType(MoveType.MOVE);
    } else {
      setMoveType(MoveType.PASS);
    }
    return move;
  }

  /** @return the {@code Strategy} of this {@code ComputerPlayer}. */
  public Strategy getStrategy() {
    return strategy;
  }

  /** @param strategy the {@code Strategy}. */
  public void setStrategy(Strategy strategy) {
    this.strategy = strategy;
  }
}
