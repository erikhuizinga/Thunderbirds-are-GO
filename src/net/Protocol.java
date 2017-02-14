package net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Created by erik.huizinga on 2-2-17. */
public interface Protocol {

  String SPACE = " ";
  String BLACK = "black";
  String WHITE = "white";

  // List<String> ALL_PROTOCOL_COMMANDS =
  //     Stream.concat(
  //            Stream.concat(
  //              Arrays.stream(GeneralCommand.values()).map(Enum::name),
  //              Arrays.stream(ServerCommand.values()).map(Enum::name)),
  //          Arrays.stream(ClientCommand.values()).map(Enum::name))
  //      .collect(Collectors.toList());

  /**
   * Validate the specified {@code String} arguments for the specified {@code ProtocolCommand} and
   * format them according to protocol. If more arguments are provided than the allowed maximum,
   * then any superfluous arguments are ignored and not returned in the argument list.
   *
   * @param protocolCommand the {@code ProtocolCommand}.
   * @param args the {@code String} arguments.
   * @return the {@code List} of {@code String} arguments.
   * @throws MalformedArgumentsException if the specified arguments are not conform the protocol.
   */
  static List<String> validateAndFormatArgList(ProtocolCommand protocolCommand, String... args)
      throws MalformedArgumentsException {
    List<String> argList = new LinkedList<>();
    Collections.addAll(
        argList, Arrays.stream(args).map(String::toLowerCase).toArray(String[]::new));
    argList = argList.subList(0, protocolCommand.maxArgs());
    if (protocolCommand.isValidArgList(argList)) {
      return argList;
    }
    throw new MalformedArgumentsException();
  }

  /**
   * Validate the specified {@code ProtocolCommand} with the specified arguments and format it as a
   * {@code String}.
   *
   * @param protocolCommand the {@code ProtocolCommand}.
   * @param args the arguments, a comma-separated {@code String} arguments.
   * @return the command as a {@code String}, ready to send over a {@code Peer} to {@code Peer}
   *     connection.
   * @throws MalformedArgumentsException if the specified arguments are not conform the protocol.
   */
  static String validateAndFormatCommandString(ProtocolCommand protocolCommand, String... args)
      throws MalformedArgumentsException {
    String result = protocolCommand.toString();
    List<String> argList = validateAndFormatArgList(protocolCommand, args);
    for (String arg : argList) {
      arg = arg.toLowerCase();
      result += SPACE + arg;
    }
    return result.trim();
  }

  /**
   * Validate the specified {@code ProtocolCommand} without arguments and format it as a {@code
   * String}.
   *
   * @param protocolCommand the {@code ProtocolCommand}.
   * @return the command as a {@code String}, ready to send over a {@code Peer} to {@code Peer}
   *     connection.
   */
  static String validateAndFormatCommandString(ProtocolCommand protocolCommand)
      throws MalformedArgumentsException {
    String result;
    result = validateAndFormatCommandString(protocolCommand, "");
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
   * Get the {@code List<String>} of arguments from the expected specified {@code ProtocolCommand}
   * on the specified {@code Scanner}.
   *
   * @param scanner the {@code Scanner}.
   * @param expectedCommands the {@code ProtocolCommand}.
   * @return the {@code List<String>} of arguments. The list is empty (size equals zero) if there
   *     are no arguments.
   * @throws UnexpectedCommandException if the the incoming command is not expected.
   */
  static List<String> expect(Scanner scanner, ProtocolCommand... expectedCommands)
      throws UnexpectedCommandException, MalformedArgumentsException {
    String commandString;

    if (scanner.hasNext() // There is next incoming communication to scan
        && (commandString = scanner.next()).toUpperCase().length()
            > 0) // The next command contains one or more characters
    {
      ProtocolCommand theProtocolCommand = null;
      for (ProtocolCommand expectedCommand : expectedCommands) {
        if (expectedCommand.toString().equals(commandString)) {
          theProtocolCommand = expectedCommand;
          break;
        }
      }

      if (theProtocolCommand != null) {
        // Read the argument list
        List<String> argList;
        try {
          String[] args = scanner.nextLine().trim().split(Protocol.SPACE);
          // argList = Arrays.asList(scanner.nextLine().trim().split(Protocol.SPACE));
          argList = validateAndFormatArgList(theProtocolCommand, args);
        } catch (NoSuchElementException | MalformedArgumentsException ignored) {
          argList = new ArrayList<>();
        }

        if (theProtocolCommand.isValidArgList(argList)) {
          return argList;
        } else {
          throw new MalformedArgumentsException();
        }
      }
    }
    throw new UnexpectedCommandException();
  }

  /** The {@code Client} protocol commands. */
  enum ClientCommand implements ProtocolCommand {
    PLAYER,
    GO;

    @Override
    public boolean isValidArgList(List<String> argList) {
      boolean isValid;
      switch (this) {
        case PLAYER:
          /*
          PLAYER name
          name: 1-20 word characters without spaces
          */
          isValid =
              argList.size() == maxArgs()
                  && argList.get(0) != null
                  && argList.get(0).matches("^\\w{1,20}$");
          break;

        case GO:
          /*
          GO dimension
          dimension: String of int where 5 <= dimension <= 131 && dimension % 2 == 1
          */
          if (argList.size() > 0) {
            try {
              int dim = Integer.parseInt(argList.get(0));
              isValid = argList.get(0) != null && isValidDimension(dim);

            } catch (NumberFormatException e) {
              isValid = false;
            }
          } else {
            isValid = false;
          }
          break;

        default:
          isValid = false;
      }
      return isValid;
    }

    @Override
    public int maxArgs() {
      switch (this) {
        case PLAYER:
          return 1;
        case GO:
          return 2;
        default:
          return 0;
      }
    }
  }

  /** The {@code Server} protocol commands. */
  enum ServerCommand implements ProtocolCommand {
    WAITING,
    READY;

    @Override
    public boolean isValidArgList(List<String> argList) {
      switch (this) {
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
          int dimension;
          try {
            dimension = Integer.parseInt(argList.get(2));
          } catch (NumberFormatException e) {
            return false;
          }
          return argList.size() == 3
              && (argList.get(0).equals(BLACK) || argList.get(0).equals(WHITE))
              && ClientCommand.PLAYER.isValidArgList(argList.subList(1, 2))
              && isValidDimension(dimension);

        default:
          return false;
      }
    }

    @Override
    public int maxArgs() {
      switch (this) {
        case WAITING:
          return 0;
        case READY:
          return 3;
        default:
          return 0;
      }
    }
  }

  /** The general protocol commands for {@code Client}-{@code Server} communication. */
  enum GeneralCommand implements ProtocolCommand {
    CHAT;

    @Override
    public boolean isValidArgList(List<String> argList) {
      switch (this) {
        case CHAT:
          /*
          CHAT string...
          string...: any number of arguments, treated as a string
            */
          return argList.size() > 0;

        default:
          return false;
      }
    }

    @Override
    public int maxArgs() {
      switch (this) {
        case CHAT:
          return Integer.MAX_VALUE;
        default:
          return 0;
      }
    }
  }

  /** The interface for every type of {@code ProtocolCommand}. */
  interface ProtocolCommand {

    /**
     * Check if the specified {@code List<String>} of arguments is valid for the {@code
     * ProtocolCommand}.
     *
     * @param argList the {@code List<String>} of arguments.
     * @return {@code true} if valid; {@code false} otherwise.
     */
    boolean isValidArgList(List<String> argList);

    /** @return the maximum number of arguments for this {@code ProtocolCommand}. */
    int maxArgs();
  }

  /**
   * The {@code Exception} for arguments of a protocol command that is malformed for the protocol.
   */
  class MalformedArgumentsException extends Exception {}

  /** The {@code Exception} for an unexpected command. */
  class UnexpectedCommandException extends Exception {}
}
