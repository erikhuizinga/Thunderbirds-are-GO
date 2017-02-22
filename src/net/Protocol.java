package net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.Set;

/** Created by erik.huizinga on 2-2-17. */
public interface Protocol {

  String SPACE = " ";
  String BLACK = "black";
  String WHITE = "white";

  //  List<String> KEYWORDS =
  //      Stream.concat(
  //              Stream.concat(
  //                  Arrays.stream(GeneralKeywords.values()).map(Enum::name),
  //                  Arrays.stream(ServerKeywords.values()).map(Enum::name)),
  //              Arrays.stream(ClientKeywords.values()).map(Enum::name))
  //          .collect(Collectors.toList());

  /** The general protocol keywords for {@code Client}-{@code Server} communication. */
  Set<Keyword> GENERAL_KEYWORDS = EnumSet.of(Keyword.CHAT);

  /** The {@code Client} protocol keywords. */
  Set<Keyword> CLIENT_KEYWORDS = EnumSet.of(Keyword.PLAYER, Keyword.GO, Keyword.CANCEL);

  /** The {@code Server} protocol keywords. */
  Set<Keyword> SERVER_KEYWORDS = EnumSet.of(Keyword.WAITING, Keyword.READY);

  /**
   * Validate the specified {@code String} arguments for the specified {@code Keyword} and format
   * them according to protocol. If more arguments are provided than the allowed maximum, then any
   * superfluous arguments are ignored and not returned in the argument list.
   *
   * @param keyword the {@code Keyword}.
   * @param args the {@code String} arguments.
   * @return the {@code List} of {@code String} arguments.
   * @throws MalformedArgumentsException if the specified arguments are not conform the protocol.
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
    return dimension >= 5 && dimension <= 131 && dimension % 2 != 0;
  }

  /**
   * Get the {@code List<String>} command of the expected specified {@code Keyword} on the specified
   * {@code Scanner}.
   *
   * @param scanner the {@code Scanner}.
   * @param expectedKeywords one {@code Keyword} or more.
   * @return the {@code List<String>} of the protocol keyword and arguments. The list contains only
   *     the keyword (size equals one) if there are no arguments.
   * @throws UnexpectedKeywordException if the the incoming keyword is not expected.
   * @throws MalformedArgumentsException if the incoming arguments are invalid for the expected
   *     keyword.
   */
  static List<String> expect(Scanner scanner, Keyword... expectedKeywords)
      throws UnexpectedKeywordException, MalformedArgumentsException {
    String keywordString;

    if (scanner.hasNext() // There is next incoming communication to scan
        && (keywordString = scanner.next()).toUpperCase().length()
            > 0) // The next keyword contains one or more characters
    {
      Keyword theKeyword = null;
      for (Keyword expectedKeyword : expectedKeywords) {
        if (expectedKeyword.toString().equals(keywordString)) {
          theKeyword = expectedKeyword;
          break;
        }
      }

      if (theKeyword != null) {
        // Read the argument list
        List<String> argList;
        try {
          String[] args = scanner.nextLine().trim().split(Protocol.SPACE);
          argList = validateAndFormatArgList(theKeyword, args);
        } catch (NoSuchElementException | MalformedArgumentsException ignored) {
          argList = new ArrayList<>();
        }

        if (theKeyword.isValidArgList(argList)) {
          argList.add(0, theKeyword.toString());
          return argList;
        } else {
          throw new MalformedArgumentsException();
        }
      }
    }
    throw new UnexpectedKeywordException();
  }

  /** The {@code Protocol} keywords. */
  enum Keyword implements Executable {
    // General
    CHAT,

    // Server
    WAITING,
    READY,

    // Client
    PLAYER,
    GO,
    CANCEL;

    private Executable action = () -> System.err.println("set me with setAction");

    public void setAction(Executable action) {
      this.action = action;
    }

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
          return isValidateArgListSize(argList);

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
          if (isValidateArgListSize(argList)) {
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

        case PLAYER:
          /*
          PLAYER name
          name: 1-20 word characters without spaces
           */
          return isValidateArgListSize(argList)
              && argList.get(0) != null
              && argList.get(0).matches("^\\w{1,20}$");

        case GO:
          /*
          GO dimension
          dimension: String of int where 5 <= dimension <= 131 && dimension % 2 == 1
           */
          if (isValidateArgListSize(argList)) {
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
        default:
          return maxArgs();
      }
    }

    /** @return the maximum number of arguments for the {@code Keyword}. */
    int maxArgs() {
      switch (this) {
        case CHAT:
          return Integer.MAX_VALUE; // Probably not a good idea, but you get the idea...
        case READY:
          return 3;
        case PLAYER:
          return 1;
        case GO:
          return 2;
        default:
          return 0;
      }
    }

    /** @return {@code true} if the specified list of arguments has a valid size. */
    boolean isValidateArgListSize(List argList) {
      return argList.size() >= minArgs() && argList.size() <= maxArgs();
    }

    @Override
    public void execute() {
      action.execute();
    }
  }

  @FunctionalInterface
  interface Executable {
    /** Execute this {@code Executable}. */
    void execute();
  }

  /** The {@code Exception} thrown for malformed arguments of a keyword. */
  class MalformedArgumentsException extends Exception {}

  /** The {@code Exception} for an unexpected keyword. */
  class UnexpectedKeywordException extends Exception {}
}
