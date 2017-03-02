package ui.tui;

import game.Go;
import game.material.Stone;
import game.material.board.Board;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;
import net.Client;
import net.Protocol;
import net.Server;
import players.ComputerPlayer;
import players.HumanPlayer;
import players.Player;
import players.RemotePlayer;
import ui.gui.GUI;

/** Created by erik.huizinga on 27-1-17. */
public class TUI implements Observer {

  /** The default menu prompt. */
  private static final String DEFAULT_MENU_PROMPT =
      "Choose an option (enter the number of choice): ";

  private static final String INDENT = " ";

  /** The input scanner. */
  private final Scanner scanner = new Scanner(System.in);

  /** The Go game. */
  private Go go;

  /** The first player (black). */
  private Player player1 = new HumanPlayer(Stone.UNKNOWN);

  /** The second player (white). */
  private Player player2 = new HumanPlayer(Stone.UNKNOWN);

  /** {@code true} if the player names have been set; {@code false} otherwise. */
  private boolean areNamesSet = false;

  /** {@code true} if identical player names are to be kept; {@code false} otherwise. */
  private boolean keepIdenticalNames = false;

  /** The single-side board dimension. */
  private int dimension;

  private UIType uiType = UIType.TUI;

  private boolean isNetGame;

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

  /**
   * Start a TUI to play a game of Go.
   *
   * @param args the input arguments; none are supported.
   */
  public static void main(String[] args) {
    new TUI();
  }

  private static void printNetPlayerConfig(Player player1) {
    System.out.println("Current player name:");
    System.out.println(INDENT + player1.getName() + " (" + player1.getGeneralization() + ")");
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
    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);
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

    isNetGame = false;

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
    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);
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

    isNetGame = true;

    player2 = new RemotePlayer(Stone.UNKNOWN);

    printNetPlayerConfig(player1);
    System.out.println();

    List<String> choiceNumbers = Arrays.asList("1", "2", "3", "99", "100");
    String[] choiceStrings =
        new String[] {
          "Accept player configuration; continue to board size selection",
          "Toggle human / computer player",
          "Change name of " + player1.getName(),
          "Go back to main menu",
          "Exit"
        };
    MenuAction[] menuActions =
        new MenuAction[] {
          () -> acceptPlayers(player1, player2, this::netMenu),
          () -> toggleHumanCPU(this::netMenu),
          () -> {
            String currentName = player1.getName();
            changePlayerNameMenu(
                player1.getName(),
                this::setPlayer1Name,
                () -> {
                  do {
                    setPlayer1Name(HumanPlayer.randomName());
                  } while (player1.getName().equals(currentName));
                },
                this::netMenu);
          },
          this::mainMenu,
          this::exitMenu
        };
    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);
  }

  /**
   * Print and handle the zero player game menu. A zero player game is played locally between two
   * computer players.
   */
  private void zeroPlayerMenu() {
    System.out.println();
    System.out.println("G------------------o");
    System.out.println("| ZERO PLAYER GAME |");
    System.out.println("o------------------G");
    System.out.println();
    System.out.println("Not yet implemented, bummer!");
    localMenu();
  }
  /**
   * Print and handle the single player game menu. A single player game is played locally between a
   * human player and a computer player.
   */
  private void singlePlayerMenu() {
    System.out.println();
    System.out.println("G--------------------o");
    System.out.println("| SINGLE PLAYER GAME |");
    System.out.println("o--------------------G");
    System.out.println();
    System.out.println("Not yet implemented, bummer!");
    localMenu();
  }

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
      setPlayer1Name(HumanPlayer.randomName());
      do {
        setPlayer2Name(HumanPlayer.randomName());
      } while (player2.getName().equals(player1.getName()));
      areNamesSet = true;
    }

    if (player1 instanceof HumanPlayer) {
      player1 = new HumanPlayer(player1.getName(), Stone.BLACK);
    } else {
      player1 = new HumanPlayer(Stone.BLACK);
    }
    if (player2 instanceof HumanPlayer) {
      player2 = new HumanPlayer(player2.getName(), Stone.WHITE);
    } else {
      player2 = new HumanPlayer(Stone.WHITE);
    }

    System.out.println(
        "Current players (note that "
            + Stone.BLACK
            + " black goes before "
            + Stone.WHITE
            + " white):");
    printPlayerConfig(player1, player2);
    System.out.println();

    if (!keepIdenticalNames && player1.getName().equals(player2.getName())) {
      System.out.println("The player names are identical, what do you want to do with them?");
      List<String> choiceNumbers = Arrays.asList("1", "2", "3");
      String[] choiceStrings =
          new String[] {
            "Change name of " + player1.displayFormat(),
            "Change name of " + player2.displayFormat(),
            "Keep identical names"
          };
      MenuAction[] menuActions =
          new MenuAction[] {
            () ->
                changePlayerNameMenu(
                    player1.displayFormat(),
                    this::setPlayer1Name,
                    () -> setPlayer1Name(HumanPlayer.randomName()),
                    this::twoPlayerMenu),
            () ->
                changePlayerNameMenu(
                    player2.displayFormat(),
                    this::setPlayer2Name,
                    () -> setPlayer2Name(HumanPlayer.randomName()),
                    this::twoPlayerMenu),
            () -> keepIdenticalNames = true
          };
      handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);
    }

    List<String> choiceNumbers = Arrays.asList("1", "2", "3", "4", "99", "100");
    String[] choiceStrings =
        new String[] {
          "Change name of " + player1.getName(),
          "Change name of " + player2.getName(),
          "Swap player colours",
          "Accept player configuration; continue to board size selection",
          "Go back to local game menu",
          "Exit"
        };
    MenuAction[] menuActions =
        new MenuAction[] {
          () ->
              changePlayerNameMenu(
                  player1.getName(),
                  this::setPlayer1Name,
                  () -> {
                    do {
                      setPlayer1Name(HumanPlayer.randomName());
                    } while (player1.getName().equals(player2.getName()));
                  },
                  this::twoPlayerMenu),
          () ->
              changePlayerNameMenu(
                  player2.getName(),
                  this::setPlayer2Name,
                  () -> {
                    do {
                      setPlayer2Name(HumanPlayer.randomName());
                    } while (player2.getName().equals(player1.getName()));
                  },
                  this::twoPlayerMenu),
          () -> swapPlayerColors(this::twoPlayerMenu),
          () ->
              acceptPlayers(
                  new HumanPlayer(player1.getName(), Stone.BLACK),
                  new HumanPlayer(player2.getName(), Stone.WHITE),
                  this::twoPlayerMenu),
          this::localMenu,
          this::exitMenu
        };
    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);
  }

  /**
   * Print and handle the change player name menu.
   *
   * @param currentName the current name.
   * @param setPName the {@code MenuAction} pointing to the method to set the correct player's name
   *     field.
   * @param randomName the {@code MenuAction} pointing to the method to generate a random name for
   *     this type of {@code Player}.
   * @param previousMenu the previous menu.
   */
  private void changePlayerNameMenu(
      String currentName, MenuAction setPName, MenuAction randomName, MenuAction previousMenu) {
    System.out.println();
    System.out.println("Changing name of " + currentName + "...");

    List<String> choiceNumbers = Arrays.asList("1", "2", "9");
    String[] choiceStrings =
        new String[] {"Set a custom name", "Set a random name", "Keep current name"};
    MenuAction[] menuActions = new MenuAction[] {setPName, randomName, () -> {}};

    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);

    // Go back to previous menu
    previousMenu.go();
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
    this.player1 = player1;
    this.player2 = player2;
    boardMenu(previousMenu);
  }

  /**
   * Print and handle the board configuration menu.
   *
   * @param previousMenu the previous menu.
   */
  private void boardMenu(MenuAction previousMenu) {
    System.out.println();
    System.out.println("What board size do you want to play on?");

    List<String> choiceNumbers = Arrays.asList("1", "9", "13", "19", "99", "100");
    String[] choiceStrings =
        new String[] {
          "Set custom size",
          "Play on 9×9",
          "Play on 13×13",
          "Play on 19×19",
          "Go back to previous menu",
          "Exit"
        };
    MenuAction[] menuActions =
        new MenuAction[] {
          this::setDimension,
          () -> dimension = 9,
          () -> dimension = 13,
          () -> dimension = 19,
          previousMenu,
          this::exitMenu
        };

    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);

    // Go to review menu
    reviewMenu(previousMenu);
  }

  /**
   * Print and handle the game review menu.
   *
   * @param previousMenu the previous menu.
   */
  private void reviewMenu(MenuAction previousMenu) {
    System.out.println();
    System.out.println("G---------------------------o");
    System.out.println("| REVIEW GAME CONFIGURATION |");
    System.out.println("o---------------------------G");
    System.out.println();

    if (player2 instanceof RemotePlayer) {
      printNetPlayerConfig(player1);

    } else {
      System.out.println(
          "Current players (note that "
              + Stone.BLACK
              + " black goes before "
              + Stone.WHITE
              + " white):");
      printPlayerConfig(player1, player2);
    }
    System.out.println();

    printBoardConfig();
    printUIConfig();

    System.out.println();
    System.out.println("Do you want to:");

    List<String> choiceNumbers = Arrays.asList("1", "2", "3", "4", "99");
    String[] choiceStrings =
        new String[] {
          isNetGame ? "Connect to server and play GO!" : "Play GO!",
          "Review player configuration",
          "Review board configuration",
          "Change UI to " + uiType.other(),
          "Go back to main menu"
        };
    MenuAction[] menuActions =
        new MenuAction[] {
          this::play,
          previousMenu,
          () -> boardMenu(() -> reviewMenu(previousMenu)),
          () -> swapUIConfiguration(() -> reviewMenu(previousMenu)),
          this::mainMenu
        };
    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);
  }

  /** Print and handle the continue menu. */
  private void continueMenu() {
    System.out.println("Do you want to play another game?");
    List<String> choiceNumbers = Arrays.asList("1", "2");
    String[] choiceStrings = new String[] {"Yes, go back to main menu", "No, exit"};
    MenuAction[] menuActions = new MenuAction[] {this::mainMenu, this::exitMenu};
    handleChoices(DEFAULT_MENU_PROMPT, choiceNumbers, choiceStrings, menuActions);
  }

  private void printPlayerConfig(Player player) {
    String displayFormat = player.displayFormat();
    System.out.println(INDENT + displayFormat);
  }

  private void printPlayerConfig(Player player1, Player player2) {
    printPlayerConfig(player1);
    printPlayerConfig(player2);
  }

  private void printBoardConfig() {
    System.out.println("Current board size is " + dimension + "×" + dimension + ".");
  }

  private void printUIConfig() {
    System.out.println("The game will be displayed on a " + uiType.toString() + ".");
  }

  /**
   * Handle the specified menu choices.
   *
   * @param prompt the prompt to display to the user.
   * @param choiceNumbers the numbers of the choices.
   * @param choiceStrings the strings of the choices.
   * @param menuActions the actions of the choices.
   */
  private void handleChoices(
      String prompt, List<String> choiceNumbers, String[] choiceStrings, MenuAction[] menuActions) {
    Map<String, MenuAction> actionMap = new HashMap<>();
    for (int i = 0; i < choiceNumbers.size(); i++) {
      System.out.println(INDENT + choiceNumbers.get(i) + ". " + choiceStrings[i]);
      actionMap.put(choiceNumbers.get(i), menuActions[i]);
    }
    System.out.println();
    actionMap.get(readPromptWithLimitedChoices(prompt, choiceNumbers)).go();
  }

  /**
   * Read a name from the input.
   *
   * @return the name.
   */
  private String readName() {
    System.out.print("Enter a new name (or leave empty to cancel): ");
    String name;
    do {
      name = (scanner.hasNextLine()) ? scanner.nextLine() : null;
    } while (name == null);
    return name;
  }

  /**
   * Swap the player colours by swapping the players.
   *
   * @param previousMenu the previous menu.
   */
  private void swapPlayerColors(MenuAction previousMenu) {
    String oldP1Name = player1.getName();
    setPlayer1Name(player2.getName());
    setPlayer2Name(oldP1Name);
    previousMenu.go();
  }

  private void toggleHumanCPU(MenuAction previousMenu) {
    if (player1 instanceof HumanPlayer) {
      player1 = new ComputerPlayer(player1.getStone());
    } else {
      player1 = new HumanPlayer(player1.getStone());
    }
    previousMenu.go();
  }

  /**
   * Swap the UI configuration.
   *
   * @param previousMenu the previous menu.
   */
  private void swapUIConfiguration(MenuAction previousMenu) {
    uiType = uiType.other();
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

  /**
   * Read input by prompting the user.
   *
   * @param prompt the prompt.
   * @return the input.
   */
  private String readPrompt(String prompt) {
    String input;
    do {
      System.out.print(prompt);
      input = (scanner.hasNextLine()) ? scanner.nextLine() : null;
    } while (input == null);
    return input;
  }

  /** Play the game. */
  private void play() {
    if (isNetGame) {
      String serverAddress =
          readPrompt("Please input a server address (or leave empty for default): ");
      if (serverAddress.equals("")) {
        serverAddress = Server.DEFAULT_ADDRESS;
      }

      String serverPort = readPrompt("Please input a server port (or leave empty for default): ");
      if (serverPort.equals("")) {
        serverPort = Server.DEFAULT_PORT;
      }
      System.out.println();

      printPlayHeader();
      System.out.println();

      Client client =
          new Client(
              player1.getName(), serverAddress, Integer.parseInt(serverPort), this, dimension);
      client.startClient();

    } else {
      System.out.println();
      printPlayHeader();

      setGo(new Go(dimension, player1, player2));
      if (uiType == UIType.GUI) {
        go.addObserver(new GUI(dimension));
      }
      go.addObserver(this);
      Thread goThread = new Thread(go);
      goThread.start();
    }
  }

  private void printPlayHeader() {
    System.out.println("G---------------o");
    System.out.println("| LET'S PLAY GO |");
    System.out.println("o---------------G");
  }

  @Override
  public void update(Observable observable, Object arg) {
    if (observable instanceof Go) {
      if (arg instanceof Board && uiType == UIType.TUI) {
        Board board = (Board) arg;
        setGo((Go) observable);
        System.out.println();
        System.out.println(board);
        System.out.println();

      } else if (arg instanceof String) {
        System.out.println(arg);
        System.out.println();

      } else if (arg == null) {
        continueMenu();
      }
    }
  }

  /**
   * Set the {@code Go} game.
   *
   * @param go the game.
   */
  private void setGo(Go go) {
    this.go = go;
  }

  private void setPlayer1Name() {
    String newName = readName();
    setPlayer1Name(newName);
  }

  private void setPlayer2Name() {
    String newName = readName();
    setPlayer2Name(newName);
  }

  private void setPlayer1Name(String name) {
    if (!name.equals("")) {
      keepIdenticalNames = false;
      player1.setName(name);
    }
  }

  private void setPlayer2Name(String name) {
    if (!name.equals("")) {
      keepIdenticalNames = false;
      player2.setName(name);
    }
  }

  private void setDimension() {
    int dimension;
    final int minDimension = Protocol.MIN_DIMENSION;
    final int maxDimension = isNetGame ? Protocol.MAX_DIMENSION : 1001;
    do {
      try {
        dimension =
            Integer.parseInt(
                readPrompt(
                    "\nEnter a custom dimension by entering just one number, i.e., \n"
                        + "the single-side board length (must be greater than or equal to "
                        + minDimension
                        + " and\n"
                        + "less than or equal to "
                        + maxDimension
                        + (isNetGame ? " and odd" : "")
                        + "): "));
      } catch (NumberFormatException e) {
        dimension = -1;
      }
    } while (isNetGame
        ? !Protocol.isValidDimension(dimension)
        : dimension < minDimension || maxDimension < dimension);
    this.dimension = dimension;
  }

  /** The type of UI used. */
  private enum UIType {
    TUI,
    GUI;

    UIType other() {
      switch (this) {
        case GUI:
          return TUI;
        case TUI:
          return GUI;
        default:
          return null;
      }
    }
  }

  //  private enum PlayerType {
  //    HUMAN,
  //    COMPUTER;
  //
  //    PlayerType other() {
  //      switch (this) {
  //        case COMPUTER:
  //          return HUMAN;
  //        case HUMAN:
  //          return COMPUTER;
  //        default:
  //          return null;
  //      }
  //    }
  //
  //    //    Player instantiate(Object... args) {
  //    //      Class[] argClasses = new Class[args.length];
  //    //      for (int i = 0; i < args.length; i++) {
  //    //        argClasses[i] = args[i].getClass();
  //    //      }
  //    //      Player player = null;
  //    //      Constructor constructor;
  //    //      try {
  //    //        switch (this) {
  //    //          case COMPUTER:
  //    //            constructor = ComputerPlayer.class.getDeclaredConstructor(argClasses);
  //    //            player = (ComputerPlayer) constructor.newInstance(args);
  //    //            break;
  //    //
  //    //          case HUMAN:
  //    //            constructor = HumanPlayer.class.getDeclaredConstructor(argClasses);
  //    //            player = (HumanPlayer) constructor.newInstance(args);
  //    //            break;
  //    //
  //    //          default:
  //    //            player = null;
  //    //        }
  //    //      } catch (InstantiationException
  //    //          | IllegalAccessException
  //    //          | NoSuchMethodException
  //    //          | InvocationTargetException e) {
  //    //        e.printStackTrace();
  //    //      }
  //    //      return player;
  //    //    }
  //  }

  //TODO make a Menu class/interface for every menu
  @FunctionalInterface
  private interface MenuAction {
    void go();
  }
}
