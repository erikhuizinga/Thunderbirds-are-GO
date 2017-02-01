package players;

import game.action.Move;
import game.action.Move.MoveType;
import game.material.Stone;
import game.material.board.Board;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/** Created by erik.huizinga on 24-1-17. */
public class HumanPlayer extends Player {

  /** The list of default {@code HumanPlayer} names. */
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

  public static final String HELP =
      "To input a command, type the command and press return.\n"
          + "To play a move, enter the row and column of the move separated by a character.\n"
          + " Example: [1][3][ ][3][return] to play on the 13th row and on the first column.\n"
          + "To pass, enter PASS.\n"
          + "To surrender, enter TABLEFLIP.\n"
          + "To see this help text, enter HELP.";

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

  /** @return a randomly generater human player name. */
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
    return nextMove();
  }

  /**
   * Get the next {@code Move} from the {@code HumanPlayer}.
   *
   * @return the {@code Move}.
   */
  public Move nextMove() {
    Move move = null;
    String input;
    String prompt = this + ", what is your choice? > ";
    int humanPlayableX = -1;
    int humanPlayableY = -1;

    Scanner scanner = new Scanner(System.in);
    System.out.print(prompt);
    input = (scanner.hasNextLine()) ? scanner.nextLine() : null;

    while (input == null) {
      prompt = this + ", what is your choice? Type HELP for instructions. > ";
      System.out.print(prompt);
      input = (scanner.hasNextLine()) ? scanner.nextLine() : null;
    }

    switch (input.toUpperCase()) {
      case "HELP":
        System.out.println(HELP);
        move = nextMove();
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
        } catch (Exception e) { //TODO specify
          e.printStackTrace();
          move = nextMove();
        }
        setMoveType(MoveType.MOVE);
        move = new Move(humanPlayableX - 1, humanPlayableY - 1, getStone());
    }
    return move;
  }
}
