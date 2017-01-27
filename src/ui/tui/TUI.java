package ui.tui;

import game.Go;
import game.material.Stone;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import players.HumanPlayer;
import players.Player;

/** Created by erik.huizinga on 27-1-17. */
public class TUI implements Observer {

  /** The default menu prompt. */
  public static final String DEFAULT_MENU_PROMPT =
      "Choose an option (enter the number of choice): ";

  /** The Go game. */
  private Go go;

  /** The first player (black). */
  private Player p1;

  /** The first player's name. */
  private String p1Name;

  /** The second player (white). */
  private Player p2;

  /** The second player's name. */
  private String p2Name;

  /** {@code true} if the player names have been set; {@code false} otherwise. */
  private boolean areNamesSet = false;

  /** {@code true} if identical player names are to be kept; {@code false} otherwise. */
  private boolean keepIdenticalNames = false;

  /** The input scanner. */
  private final Scanner scanner = new Scanner(System.in);

  /** The single-side board dimension. */
  private int dim;

  /** Instantiate a {@code TUI} to play Go. */
  public TUI() {
    // Welcome
    System.out.println("Thunderbirds are...");

    System.out.println(
        "         _             _         _\n"
            + "        /\\ \\          /\\ \\      /\\_\\   \n"
            + "       /  \\ \\        /  \\ \\    / / /   \n"
            + "      / /\\ \\_\\      / /\\ \\ \\  / / /_\n"
            + "     / / /\\/_/     / / /\\ \\ \\/ /___/\\  \n"
            + "    / / / ______  / / /  \\ \\_\\____ \\ \\ \n"
            + "   / / / /\\_____\\/ / /   / / /   / / / \n"
            + "  / / /  \\/____ / / /   / / /   / / /  \n"
            + " / / /_____/ / / / /___/ / /   _\\/_/   \n"
            + "/ / /______\\/ / / /____\\/ /   /\\_\\     \n"
            + "\\/___________/\\/_________/    \\/_/");

    /* Menu:
     * 1.1 Local
     *  1.1.1 PvP
     *  1.1.2 PvC
     *  1.1.3 CvC
     *
     * or
     *
     * 1.2 Network
     *  1.2.1 Pv?
     *  1.2.2 Cv?
     *
     * then
     *
     * 2.1 Name(s)
     *  2.1.1 Player 1 name
     *   2.1.1.1 Auto-choose name or custom
     *  2.1.2 Player 2 name
     *   2.1.2.1 Auto-choose name or custom
     *
     * or
     *
     * 2.2. Colour
     *  2.2.1 Player 1 vs. Player 2 colour (toggle)
     *
     * or
     *
     * 3.1. Board size
     *  3.1.1 9x9
     *  3.1.2 13x13
     *  3.1.3 19x19
     *  3.1.4 custom
     *
     * then
     * 4.1 Review game config
     *  4.1.1. Back to player configuration
     *  4.1.2. Back to board configuration
     *  4.1.3. Back to main menu
     *  4.1.4. Play!
     */

    // Main menu
    mainMenu();
  }

  /** An adapter to allow pointers to functions. */
  private interface MenuAction {

    void go();
  }
  /** Print and handle the main menu. */
  private void mainMenu() {
    System.out.println();
    System.out.println("G-----------o");
    System.out.println("| MAIN MENU |");
    System.out.println("o-----------G");
    System.out.println();

    List<String> choiceNumbers = Arrays.asList("1", "2", "100");
    String[] choiceStrings = new String[] {"Local game", "Network game", "Exit"};
    MenuAction[] menuActions = new MenuAction[] {this::localMenu, this::netMenu, this::exitMenu};
    Map<String, MenuAction> actionMap = new HashMap<>();
    for (int i = 0; i < choiceNumbers.size(); i++) {
      System.out.println(" " + choiceNumbers.get(i) + ". " + choiceStrings[i]);
      actionMap.put(choiceNumbers.get(i), menuActions[i]);
    }
    System.out.println();

    actionMap.get(readPromptWithLimitedChoices(DEFAULT_MENU_PROMPT, choiceNumbers)).go();
  }

  /** Exit the {@code TUI}. */
  private void exitMenu() {
    System.out.println();
    System.out.println("G----------o");
    System.out.println("| GOODBYE! |");
    System.out.println("o----------G");
    System.exit(0);
  }

  /**
   * Print and handle the local game menu. A local game is not played over a Client-Server
   * interface.
   */
  private void localMenu() {
    System.out.println();
    System.out.println("G------------o");
    System.out.println("| LOCAL GAME |");
    System.out.println("o------------G");
    System.out.println();

    List<String> choiceNumbers = Arrays.asList("0", "1", "2", "99", "100");
    String[] choiceStrings =
        new String[] {
          "Zero player game (computer vs. computer)",
          "Single player game (player vs. computer)",
          "Two player game (player vs. player)",
          "Back to main menu",
          "Exit"
        };
    MenuAction[] menuActions =
        new MenuAction[] {
          this::zeroPlayerMenu,
          this::singlePlayerMenu,
          this::twoPlayerMenu,
          this::mainMenu,
          this::exitMenu
        };
    Map<String, MenuAction> actionMap = new HashMap<>();
    for (int i = 0; i < choiceNumbers.size(); i++) {
      System.out.println(" " + choiceNumbers.get(i) + ". " + choiceStrings[i]);
      actionMap.put(choiceNumbers.get(i), menuActions[i]);
    }
    System.out.println();

    actionMap.get(readPromptWithLimitedChoices(DEFAULT_MENU_PROMPT, choiceNumbers)).go();
  }

  /**
   * Print and handle the network game menu. A network game is played over a Client-Server
   * interface.
   */
  private void netMenu() {
    System.out.println();
    System.out.println("G--------------o");
    System.out.println("| NETWORK GAME |");
    System.out.println("o--------------G");
    System.out.println();
    System.out.println("Not yet implemented, bummer!");
    mainMenu();
  }

  /**
   * Print and handle the zero player game menu. A zero player game is played locally between two
   * computer players.
   */
  private void zeroPlayerMenu() {}

  /**
   * Print and handle the single player game menu. A single player game is played locally between a
   * human player and a computer player.
   */
  private void singlePlayerMenu() {}

  /**
   * Print and handle the two player game menu. A two player game is played locally between two
   * human players.
   */
  private void twoPlayerMenu() {
    System.out.println();
    System.out.println("G-----------------o");
    System.out.println("| TWO PLAYER GAME |");
    System.out.println("o-----------------G");
    System.out.println();

    if (!areNamesSet) {
      setP1Name(HumanPlayer.randomName());
      do {
        setP2Name(HumanPlayer.randomName());
      } while (p2Name.equals(p1Name));
      areNamesSet = true;
    }

    System.out.println(
        "Current players (note that "
            + Stone.BLACK
            + " black goes before "
            + Stone.WHITE
            + " white):");
    String p1Display = HumanPlayer.displayFormat(p1Name, Stone.BLACK);
    System.out.println(" " + p1Display);
    String p2Display = HumanPlayer.displayFormat(p2Name, Stone.WHITE);
    System.out.println(" " + p2Display);
    System.out.println();

    if (!keepIdenticalNames && p1Name.equals(p2Name)) {
      System.out.println("The player names are identical, what do you want to do with them?");
      List<String> choiceNumbers = Arrays.asList("1", "2", "3");
      String[] choiceStrings =
          new String[] {
            "Change name of " + p1Display, "Change name of " + p2Display, "Keep identical names"
          };
      MenuAction[] menuActions =
          new MenuAction[] {
            () ->
                changePlayerNameMenu(
                    p1Display,
                    this::setP1Name,
                    () -> setP1Name(HumanPlayer.randomName()),
                    this::twoPlayerMenu),
            () ->
                changePlayerNameMenu(
                    p2Display,
                    this::setP2Name,
                    () -> setP2Name(HumanPlayer.randomName()),
                    this::twoPlayerMenu),
            () -> keepIdenticalNames = true
          };
      Map<String, MenuAction> actionMap = new HashMap<>();
      for (int i = 0; i < choiceNumbers.size(); i++) {
        System.out.println(" " + choiceNumbers.get(i) + ". " + choiceStrings[i]);
        actionMap.put(choiceNumbers.get(i), menuActions[i]);
      }
      System.out.println();

      actionMap.get(readPromptWithLimitedChoices(DEFAULT_MENU_PROMPT, choiceNumbers)).go();
    }

    List<String> choiceNumbers = Arrays.asList("1", "2", "3", "4", "99", "100");
    String[] choiceStrings =
        new String[] {
          "Change name of " + p1Name,
          "Change name of " + p2Name,
          "Swap player colours",
          "Accept player configuration; continue to board size selection",
          "Back to local game menu",
          "Exit"
        };
    MenuAction[] menuActions =
        new MenuAction[] {
          () ->
              changePlayerNameMenu(
                  p1Name,
                  this::setP1Name,
                  () -> setP1Name(HumanPlayer.randomName()),
                  this::twoPlayerMenu),
          () ->
              changePlayerNameMenu(
                  p2Name,
                  this::setP2Name,
                  () -> setP2Name(HumanPlayer.randomName()),
                  this::twoPlayerMenu),
          () -> swapPlayerColors(this::twoPlayerMenu),
          () ->
              acceptPlayers(
                  new HumanPlayer(p1Name, Stone.BLACK),
                  new HumanPlayer(p2Name, Stone.WHITE),
                  this::twoPlayerMenu),
          this::localMenu,
          this::exitMenu
        };
    Map<String, MenuAction> actionMap = new HashMap<>();
    for (int i = 0; i < choiceNumbers.size(); i++) {
      System.out.println(" " + choiceNumbers.get(i) + ". " + choiceStrings[i]);
      actionMap.put(choiceNumbers.get(i), menuActions[i]);
    }
    System.out.println();

    actionMap.get(readPromptWithLimitedChoices(DEFAULT_MENU_PROMPT, choiceNumbers)).go();
  }

  /**
   * Accept the player configuration from a previous menu, store two appropriate {@code Player}
   * instances and continue to the board configuration menu.
   *
   * @param player1 the first (black) {@code Player}.
   * @param player2 the second (white) {@code Player}.
   * @param previousMenu the previous menu.
   */
  private void acceptPlayers(Player player1, Player player2, MenuAction previousMenu) {
    setP1(player1);
    setP2(player2);
    boardMenu(previousMenu);
  }

  private void boardMenu(MenuAction previousMenu) {
    System.out.println();
    System.out.println("What board size do you want to play on?");

    List<String> choiceNumbers = Arrays.asList("19", "13", "9", "1", "99", "100");
    String[] choiceStrings =
        new String[] {"19×19", "13×13", "9×9", "Set custom size", "Back to previous menu", "Exit"};
    MenuAction[] menuActions =
        new MenuAction[] {
          () -> setDim(19),
          () -> setDim(13),
          () -> setDim(9),
          this::setDim,
          () -> previousMenu.go(),
          this::exitMenu
        };

    Map<String, MenuAction> actionMap = new HashMap<>();
    for (int i = 0; i < choiceNumbers.size(); i++) {
      System.out.println(" " + choiceNumbers.get(i) + ". " + choiceStrings[i]);
      actionMap.put(choiceNumbers.get(i), menuActions[i]);
    }
    System.out.println();

    actionMap.get(readPromptWithLimitedChoices(DEFAULT_MENU_PROMPT, choiceNumbers)).go();

    // Go to review menu
    reviewMenu(previousMenu);
  }

  private void reviewMenu(MenuAction previousMenu) {
  }

  private String readName() {
    System.out.print("Enter a new name (or leave empty to cancel): ");
    String name;
    do {
      name = (scanner.hasNextLine()) ? scanner.nextLine() : null;
    } while (name == null);
    return name;
  }

  private void swapPlayerColors(MenuAction previousMenu) {
    String oldP1Name = p1Name;
    setP1Name(p2Name);
    setP2Name(oldP1Name);
    previousMenu.go();
  }

  private void changePlayerNameMenu(
      String currentName, MenuAction setPName, MenuAction randomName, MenuAction previousMenu) {
    System.out.println();
    System.out.println("Changing name of " + currentName + "...");

    List<String> choiceNumbers = Arrays.asList("1", "2", "9");
    String[] choiceStrings =
        new String[] {"Set a custom name", "Set a random name", "Keep current name"};
    MenuAction[] menuActions =
        new MenuAction[] {() -> setPName.go(), () -> randomName.go(), () -> {}};

    Map<String, MenuAction> actionMap = new HashMap<>();
    for (int i = 0; i < choiceNumbers.size(); i++) {
      System.out.println(" " + choiceNumbers.get(i) + ". " + choiceStrings[i]);
      actionMap.put(choiceNumbers.get(i), menuActions[i]);
    }
    System.out.println();

    actionMap.get(readPromptWithLimitedChoices(DEFAULT_MENU_PROMPT, choiceNumbers)).go();

    // Go back to previous menu
    previousMenu.go();
  }

  /**
   * Prompt a user for input with a limited set of legal input choices. If illegal input is given,
   * the user is prompted again until legal input is given.
   *
   * @param prompt the prompt to display.
   * @param choices the {@code List<String>} of legal choices.
   * @return the choice, an element of {@code mainMenuChoices}.
   */
  private String readPromptWithLimitedChoices(String prompt, List<String> choices) {
    String input;
    do {
      input = readPrompt(prompt);
    } while (!choices.contains(input));
    return input;
  }

  private String readPrompt(String prompt) {
    String input;
    do {
      System.out.print(prompt);
      input = (scanner.hasNextLine()) ? scanner.nextLine() : null;
    } while (input == null);
    return input;
  }

  @Override
  public void update(Observable o, Object arg) {}

  /**
   * Set the {@code Go} game.
   *
   * @param go the game.
   */
  private void setGo(Go go) {
    this.go = go;
  }

  private void setP1Name() {
    String newName = readName();
    setP1Name(newName);
  }

  private void setP2Name() {
    String newName = readName();
    setP2Name(newName);
  }

  private void setP1Name(String name) {
    if (!name.equals("") && name != null) {
      keepIdenticalNames = false;
      p1Name = name;
    }
  }

  private void setP2Name(String name) {
    if (!name.equals("") && name != null) {
      keepIdenticalNames = false;
      p2Name = name;
    }
  }

  private void setP1(Player p1) {
    this.p1 = p1;
  }

  private void setP2(Player p2) {
    this.p2 = p2;
  }

  private void setDim(int dim) {
    this.dim = dim;
  }

  private void setDim() {
    int dim;
    do {
      dim =
          Integer.parseInt(
              readPrompt(
                  "Enter a custom dimension by entering just one number, i.e., \n"
                      + "this single-side board length (must be greater than or equal to 5 and\n"
                      + "less than or equal to 1001): "));
    } while (dim < 5 || 1001 < dim);
    setDim(dim);
  }

  /**
   * Start a TUI to play a game of Go.
   *
   * @param args the input arguments; none are supported.
   */
  public static void main(String[] args) {
    TUI tui = new TUI();
  }
}
