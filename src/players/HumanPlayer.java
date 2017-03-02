package players;

import game.action.Move;
import game.action.Move.MoveType;
import game.material.Stone;
import game.material.board.Board;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Created by erik.huizinga on 24-1-17. */
public class HumanPlayer extends Player {

  /** The list of default {@code HumanPlayer} names. */
  public static final List<String> NAMES =
      Arrays.asList(
          "Jeff",
          "Scott",
          "Virgil",
          "Alan",
          "Gordon",
          "John",
          "Brains",
          "Penelope",
          "Parker",
          "Rinus",
          "Barry");

  public static final String HELP =
      "\n - To input a command, type the command and press \u2B90 (return).\n"
          + " - To play a move, enter the row and column of the move separated by a character.\n"
          + "     Example: enter 5␣3\u2B90"
          + " to play on the fifth row and on the third column.\n"
          + " - To pass, enter PASS.\n"
          + " - To surrender, enter TABLEFLIP.\n"
          + " - To see this help text, enter HELP.\n";

  public static final String DEFAULT_PROMPT_SUFFIX = ", what is your next move?";
  public static final String PROMPT_HELP_SUFFIX = " Type HELP for instructions.";

  private static String generalization = "human";

  /**
   * Instantiate a {@code Human player} with a random name and the specified {@code Stone}.
   *
   * @param stone the stone
   */
  public HumanPlayer(Stone stone) {
    super(stone, randomName());
  }

  /**
   * Instantiate as {@code HumanPlayer} with the specified name and {@code Stone}.
   *
   * @param name the name.
   * @param stone the {@code Stone}.
   */
  public HumanPlayer(String name, Stone stone) {
    super(stone, name);
  }

  /** @return a randomly generated human player name. */
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
    return nextMove(this + DEFAULT_PROMPT_SUFFIX);
  }

  /**
   * Get the next {@code Move} from the {@code HumanPlayer} through {@code System.in} while showing
   * the specified prompt.
   *
   * @param prompt the prompt.
   * @return the {@code Move}.
   */
  public Move nextMove(String prompt) {
    Move move = null;
    String input;
    String thePrompt = prompt + " > ";
    int humanPlayableX = -1;
    int humanPlayableY = -1;

    Scanner scanner = new Scanner(System.in);
    do {
      System.out.print(thePrompt);
      input = (scanner.hasNextLine()) ? scanner.nextLine() : null;
    } while (input == null);

    switch (input.toUpperCase()) {
      case "HELP":
        System.out.println(HELP);
        move = nextMove(this + DEFAULT_PROMPT_SUFFIX);
        break;

      case "PASS":
        setMoveType(MoveType.PASS);
        break;

      case "TABLEFLIP":
        setMoveType(MoveType.TABLEFLIP);
        break;

      default:
        // Try to recognise human playable indices from the string
        input = input.replaceAll("\\D", " ").trim();
        try {
          Scanner intScanner = new Scanner(input);
          humanPlayableX = intScanner.nextInt();
          humanPlayableY = intScanner.nextInt();
        } catch (NoSuchElementException e) { // Thrown when nothing is entered
          move =
              nextMove(prompt.contains(PROMPT_HELP_SUFFIX) ? prompt : prompt + PROMPT_HELP_SUFFIX);
          break;
        } catch (Exception e) { //TODO specify
          e.printStackTrace();
          move =
              nextMove(prompt.contains(PROMPT_HELP_SUFFIX) ? prompt : prompt + PROMPT_HELP_SUFFIX);
          break;
        }
        setMoveType(MoveType.MOVE);
        move = new Move(humanPlayableX - 1, humanPlayableY - 1, getStone());
    }
    return move;
  }
}
