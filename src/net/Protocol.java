package net;

import game.material.Stone;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

/** Created by erik.huizinga on 2-2-17. */
public interface Protocol {

  String SPACE = " ";
  String BLACK = Stone.BLACK.name().toLowerCase();
  String WHITE = Stone.WHITE.name().toLowerCase();

  List<String> DEFAULT_EMPTY_ARGUMENT_LIST = Collections.emptyList();
  Executable DEFAULT_EXECUTABLE = Protocol::doNothing;
  int MIN_DIMENSION = 5;
  int MAX_DIMENSION = 131;

  //  List<String> KEYWORDS =
  //      Stream.concat(
  //              Stream.concat(
  //                  Arrays.stream(GeneralKeywords.values()).map(Enum::name),
  //                  Arrays.stream(ServerKeywords.values()).map(Enum::name)),
  //              Arrays.stream(ClientKeywords.values()).map(Enum::name))
  //          .collect(Collectors.toList());
  //
  //  /** The general protocol keywords for {@code Client}-{@code Server} communication. */
  //  Set<Keyword> GENERAL_KEYWORDS = EnumSet.of(Keyword.CHAT);
  //
  //  /** The {@code Client} protocol keywords. */
  //  Set<Keyword> CLIENT_KEYWORDS = EnumSet.of(Keyword.PLAYER, Keyword.GO, Keyword.CANCEL);
  //
  //  /** The {@code Server} protocol keywords. */
  //  Set<Keyword> SERVER_KEYWORDS = EnumSet.of(Keyword.WAITING, Keyword.READY);

  /**
   * Validate the specified {@code String} arguments for the specified {@code Keyword} and format
   * them according to protocol. If more arguments are provided than the allowed maximum, then any
   * superfluous arguments are ignored and not returned in the argument list.
   *
   * @param keyword the {@code Keyword}.
   * @param args the {@code String} arguments.
   * @return the {@code List<String>} of arguments.
   * @throws MalformedArgumentsException if the specified arguments are malformed.
   */
  static List<String> validateAndFormatArgList(Keyword keyword, String... args)
      throws MalformedArgumentsException {
    List<String> argList = new LinkedList<>();
    Collections.addAll(
        argList, Arrays.stream(args).map(String::toLowerCase).toArray(String[]::new));

    // Get up to the maximum number of allowed arguments from the specified list of arguments
    int toIndex = Math.min(keyword.maxArgs(), argList.size());
    argList = argList.subList(0, toIndex);

    // Return if valid; throw an exception otherwise
    if (keyword.isValidArgList(argList)) {
      return argList;
    }
    throw new MalformedArgumentsException();
  }

  /**
   * Validate the specified {@code String} arguments for the specified {@code Command} and format
   * them according to protocol. If more arguments are provided than the allowed maximum, then any
   * superfluous arguments are ignored and not returned in the argument list.
   *
   * @param command the {@code Command}.
   * @param args the {@code String} arguments.
   * @return the {@code List<String>} of arguments.
   * @throws MalformedArgumentsException if the specified arguments are malformed.
   */
  static List<String> validateAndFormatArgList(Command command, String... args)
      throws MalformedArgumentsException {
    return validateAndFormatArgList(command.getKeyword(), args);
  }

  /**
   * Validate the specified {@code Keyword} with the specified arguments and format it as a {@code
   * String}.
   *
   * @param keyword the {@code Keyword}.
   * @param args the arguments, a comma-separated {@code String} arguments.
   * @return the command as a {@code String}, ready to send over a {@code Peer} to {@code Peer}
   *     connection.
   * @throws MalformedArgumentsException if the specified arguments are not conform the protocol.
   */
  static String validateAndFormatCommandString(Keyword keyword, String... args)
      throws MalformedArgumentsException {
    String result = keyword.toString();
    List<String> argList = validateAndFormatArgList(keyword, args);
    for (String arg : argList) {
      arg = arg.toLowerCase();
      result += SPACE + arg;
    }
    return result.trim();
  }

  /**
   * Validate the specified {@code Keyword} without arguments and format it as a {@code String}.
   *
   * @param keyword the {@code Keyword}.
   * @return the command as a {@code String}, ready to send over a {@code Peer} to {@code Peer}
   *     connection.
   */
  static String validateAndFormatCommandString(Keyword keyword) throws MalformedArgumentsException {
    String result;
    result = validateAndFormatCommandString(keyword, "");
    return result;
  }

  /**
   * Check if the specified dimension is valid conform protocol. It is valid if it is between 5 and
   * 131 inclusive and odd.
   *
   * @param dimension the dimension.
   * @return {@code true} if valid; {@code false} otherwise.
   */
  static boolean isValidDimension(int dimension) {
    return dimension >= MIN_DIMENSION && dimension <= MAX_DIMENSION && dimension % 2 != 0;
  }

  /**
   * Get a {@code Command} with any arguments from the specified {@code Scanner} if it is one of the
   * specified expected commands. This method may block until the {@code Scanner} has next input.
   *
   * @param scanner the {@code Scanner}, which must not be closed.
   * @param expectedCommands one or more expected {@code Command} instances.
   * @return an expected {@code Command} with the corresponding arguments (there may be zero)
   *     gettable with {@code getArgs} or {@code getArgList}.
   * @throws UnexpectedKeywordException if the the incoming command is not expected.
   * @throws MalformedArgumentsException if the incoming arguments are malformed for the expected
   *     command.
   */
  static Command expect(Scanner scanner, Command... expectedCommands)
      throws UnexpectedKeywordException, MalformedArgumentsException {
    String keywordString;
    if (scanner.hasNext() // There is next incoming communication to scan
        && (keywordString = scanner.next()).toUpperCase().length()
            > 0) // The next keyword contains any characters
    {
      Command theCommand = null;
      for (Command expectedCommand : expectedCommands) {
        if (expectedCommand.getKeyword().toString().equals(keywordString)) {
          theCommand = expectedCommand;
          break;
        }
      }

      if (theCommand != null) {
        // Read the argument list
        List<String> argList;
        try {
          String[] args = scanner.nextLine().trim().split(Protocol.SPACE);
          argList = validateAndFormatArgList(theCommand, args);
        } catch (NoSuchElementException | MalformedArgumentsException ignored) {
          argList = DEFAULT_EMPTY_ARGUMENT_LIST;
        }
        theCommand.setArgList(argList);
        return theCommand;
      }
    }
    throw new UnexpectedKeywordException();
  }

  /**
   * Get a {@code Command} with any arguments from the specified {@code Scanner} if it is one of the
   * specified expected {@code Keyword} instances. This method may block until the {@code Scanner}
   * has next input.
   *
   * @param scanner the {@code Scanner}, which must not be closed.
   * @param expectedKeywords one or more expected {@code Keyword} instances.
   * @return an expected {@code Command} with the corresponding arguments (there may be zero)
   *     gettable with {@code getArgs} or {@code getArgList}.
   * @throws UnexpectedKeywordException if the the incoming command is not expected.
   * @throws MalformedArgumentsException if the incoming arguments are malformed for the expected
   *     command.
   */
  static Command expect(Scanner scanner, Keyword... expectedKeywords)
      throws UnexpectedKeywordException, MalformedArgumentsException {
    Command[] expectedCommands =
        Arrays.stream(expectedKeywords)
            .map(Command::new)
            .collect(Collectors.toList())
            .toArray(new Command[] {});
    return expect(scanner, expectedCommands);
  }

  static void doNothing(List ignored) {}

  static void chatPrinter(List<String> message) {
    for (String word : message) {
      System.out.print(word + SPACE);
    }
    System.out.println();
  }

  /** The {@code Protocol} keywords. */
  enum Keyword {
    // General
    CHAT,

    // Server
    WAITING,
    READY,
    WARNING,

    // Client
    PLAYER,
    GO,
    CANCEL,
    EXIT,
    MOVE;

    /**
     * Check if the specified {@code List<String>} of arguments is valid for the {@code Keyword}.
     *
     * @param argList the {@code List<String>} of arguments.
     * @return {@code true} if valid; {@code false} otherwise.
     */
    boolean isValidArgList(List<String> argList) {
      switch (this) {
        case CHAT:
          /*
          CHAT string...
          string...: any number of arguments, treated as a string
           */
          return isValidArgListSize(argList);

        case WAITING:
          /*
          WAITING
          No arguments, ignore any
           */
          return true;

        case READY:
          /*
          READY thisColour opponentName dimension
          thisColour: this player's colour
          opponentName: opponent player's name
          dimension: board dimension, see keyword GO
           */
          if (isValidArgListSize(argList)) {
            int dimension;
            try {
              dimension = Integer.parseInt(argList.get(2));
            } catch (NumberFormatException e) {
              return false;
            }
            return (argList.get(0).equals(BLACK) || argList.get(0).equals(WHITE))
                && Keyword.PLAYER.isValidArgList(argList.subList(1, 2))
                && isValidDimension(dimension);
          }

        case WARNING:
          /*
          WARNING string...
          string...: any number of arguments, treated as a string
           */
          return isValidArgListSize(argList);

        case PLAYER:
          /*
          PLAYER name
          name: 1-20 word characters without spaces
           */
          return isValidArgListSize(argList)
              && argList.get(0) != null
              && argList.get(0).matches("^\\w{1,20}$");

        case GO:
          /*
          GO dimension
          dimension: String of int where 5 <= dimension <= 131 && dimension % 2 == 1
           */
          if (isValidArgListSize(argList)) {
            if (argList.size() == maxArgs()
                && !Keyword.PLAYER.isValidArgList(argList.subList(1, 2))) {
              return false;
            }
            try {
              int dim = Integer.parseInt(argList.get(0));
              return argList.get(0) != null && isValidDimension(dim);

            } catch (NumberFormatException e) {
              return false;
            }
          } else {
            return false;
          }

        case CANCEL:
          /*
          CANCEL
          No arguments, ignore any
           */
          return true;

        case EXIT:
          /*
          EXIT
          No arguments, ignore any
           */
          return true;

        case MOVE:
          /*
          MOVE x y
          x: x coordinate of the move
          y: y coordinate of the move
           */
          int x;
          int y;
          if (isValidArgListSize(argList)) {
            try {
              x = Integer.parseInt(argList.get(0));
              y = Integer.parseInt(argList.get(1));
            } catch (NumberFormatException e) {
              return false;
            }
          } else {
            return false;
          }
          return x > 0 && y > 0;

        default:
          return false;
      }
    }

    /** @return the minimum number of arguments for the {@code Keyword}. */
    int minArgs() {
      switch (this) {
        case CHAT:
          return 1;
        case GO:
          return 1;
        case WARNING:
          return 1;
        case MOVE:
          return 2;
        default:
          return maxArgs();
      }
    }

    /** @return the maximum number of arguments for the {@code Keyword}. */
    int maxArgs() {
      switch (this) {
        case CHAT:
          return Integer.MAX_VALUE; // Probably not a good idea...
        case READY:
          return 3;
        case WARNING:
          return Integer.MAX_VALUE; // Probably not a good idea...
        case PLAYER:
          return 1;
        case GO:
          return 2;
        case MOVE:
          return 2;
        default:
          return 0;
      }
    }

    /** @return {@code true} if the specified list of arguments has a valid size. */
    boolean isValidArgListSize(List argList) {
      return argList.size() >= minArgs() && argList.size() <= maxArgs();
    }
  }

  @FunctionalInterface
  interface Executable {

    /**
     * Execute this {@code Executable} with the specified {@code List<String>} of arguments.
     *
     * @param argList the {@code List<String>} of arguments.
     */
    void execute(List<String> argList);
  }

  class Command implements Executable {

    private final Keyword keyword;
    private boolean isExecutableSet = false;
    private List<String> argList = DEFAULT_EMPTY_ARGUMENT_LIST;
    private Executable executable = DEFAULT_EXECUTABLE;

    public Command(Keyword keyword, List<String> argList) {
      this.keyword = keyword;
      this.argList = argList;
    }

    public Command(Keyword keyword) {
      this.keyword = keyword;
    }

    public Keyword getKeyword() {
      return keyword;
    }

    @Override
    public String toString() {
      String argListString = "";
      for (String arg : argList) {
        argListString += arg + SPACE;
      }
      String commandString = keyword + SPACE + argListString;
      commandString = commandString.trim();
      return "Command{" + commandString + "}";
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof Command && toString().equals(obj.toString());
    }

    /**
     * @return the {@code List<String>} of arguments.
     * @throws MalformedArgumentsException if the list of arguments is malformed.
     */
    List<String> getArgList() throws MalformedArgumentsException {
      if (!keyword.isValidArgList(argList)) {
        throw new MalformedArgumentsException();
      }
      return argList;
    }

    /**
     * @param argList the {@code List<String>} of arguments.
     * @throws MalformedArgumentsException if the list of arguments is malformed.
     */
    void setArgList(List<String> argList) throws MalformedArgumentsException {
      if (!keyword.isValidArgList(argList)) {
        throw new MalformedArgumentsException();
      }
      this.argList = argList;
    }

    /**
     * @return the arguments of this {@code Keyword} as a {@code String[]}.
     * @throws MalformedArgumentsException if the list of arguments is malformed.
     */
    public String[] getArgs() throws MalformedArgumentsException {
      return getArgList().toArray(new String[] {});
    }

    public void setExecutable(Executable executable) {
      this.executable = executable;
      isExecutableSet = true;
    }

    @Override
    public void execute(List<String> argList) {
      if (!isExecutableSet) {
        System.err.println("executable not set, not executing");
        return;
      }
      executable.execute(argList);
    }

    /** Execute the {@code Executable} of this {@code Command} with this command's argument list. */
    public void execute() {
      execute(argList);
    }

    /** Print this {@code Command} and execute its {@code Executable}. */
    public void printAndExecute() {
      System.out.println(this);
      execute();
    }
  }

  /** The {@code Exception} thrown for malformed arguments of a keyword. */
  class MalformedArgumentsException extends Exception {}

  /** The {@code Exception} for an unexpected keyword. */
  class UnexpectedKeywordException extends Exception {}
}
