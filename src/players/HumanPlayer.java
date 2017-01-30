package players;

import game.action.Move;
import game.material.Stone;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/** Created by erik.huizinga on 24-1-17. */
public class HumanPlayer extends Player {

  public static final List<String> NAMES =
      Arrays.asList(
          "Jeff Tracy",
          "Scott Tracy",
          "Virgil Tracy",
          "Alan Tracy",
          "Gordon Tracy",
          "John Tracy",
          "Brains",
          "Lady Penelope Creighton-Ward",
          "Aloysius Parker",
          "Zanger Rinus",
          "Barry Badpak");

  /** The type of {@code Player}. */
  public static final String REALIZATION = "human";

  /**
   * Instantiate a {@code Human player} with a random name and the specified {@code Stone}.
   *
   * @param stone the stone
   */
  public HumanPlayer(Stone stone) {
    super(util.ListTools.random(NAMES), stone);
  }

  /**
   * Instantiate as {@code HumanPlayer} with the specified name and {@code Stone}.
   *
   * @param name the name.
   * @param stone the {@code Stone}.
   */
  public HumanPlayer(String name, Stone stone) {
    super(name, stone);
  }

  /** @return a randomly generater human player name. */
  public static String randomName() {
    return randomName(NAMES);
  }

  public static String displayFormat(String name, Stone stone) {
    return Player.displayFormat(name, stone) + ", " + REALIZATION;
  }

  @Override
  public Move nextMove() {
    Move move;
    String input;
    String prompt = "> " + getName() + " (" + getStone() + "), what is your choice? ";
    Scanner scanner = new Scanner(System.in);
    do {
      System.out.print(prompt);
      input = (scanner.hasNextLine()) ? scanner.nextLine() : null;
    } while (input == null);
    int humanPlayableX = -1;
    int humanPlayableY = -1;
    switch (input.toUpperCase()) {
      case "HELP":
        //TODO
      case "PASS":
        //TODO
      default:
        // Try to recognise human playable indices from the string
        input = input.replaceAll("\\D", " ").trim();
        try {
          Scanner intScanner = new Scanner(input);
          humanPlayableX = intScanner.nextInt();
          humanPlayableY = intScanner.nextInt();
        } catch (Exception e) { //TODO specify
          e.printStackTrace();
          move = nextMove();
        }
    }
    move = new Move(humanPlayableX - 1, humanPlayableY - 1, getStone());
    return move;
  }
}
