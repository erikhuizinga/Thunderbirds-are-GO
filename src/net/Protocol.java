package net;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/** Created by erik.huizinga on 2-2-17. */
public interface Protocol {

  String SPACE = " ";
  String BLACK = "BLACK";
  String WHITE = "WHITE";

  // List<String> ALL_PROTOCOL_COMMANDS =
  //     Stream.concat(
  //            Stream.concat(
  //              Arrays.stream(GeneralCommand.values()).map(Enum::name),
  //              Arrays.stream(ServerCommand.values()).map(Enum::name)),
  //          Arrays.stream(ClientCommand.values()).map(Enum::name))
  //      .collect(Collectors.toList());

  /**
   * Validate the specified {@code ProtocolCommand} with the specified arguments and format it as a
   * {@code String}.
   *
   * @param protocolCommand the {@code ProtocolCommand}.
   * @param args the arguments, a comma-separated {@code String} arguments.
   * @return the command as a {@code String}, ready to send over a {@code Peer} to {@code Peer}
   *     connection.
   * @throws MalformedCommandException thrown if the specified arguments are not conform the
   *     protocol.
   */
  static String validateAndFormatCommand(ProtocolCommand protocolCommand, String... args)
      throws MalformedCommandException {
    String result = protocolCommand.toString();
    List<String> argList = new LinkedList<>();
    for (String arg : args) {
      arg = arg.toUpperCase();
      argList.add(arg);
      result += SPACE + arg;
    }
    if (!protocolCommand.isValidArgList(argList)) {
      throw new MalformedCommandException("command malformed by protocol: " + result);
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
   * @throws MalformedCommandException thrown if the specified arguments are not conform the
   *     protocol.
   */
  static String validateAndFormatCommand(ProtocolCommand protocolCommand) {
    String result = null;
    try {
      result = validateAndFormatCommand(protocolCommand, "");
    } catch (MalformedCommandException ignored) {
      // Ignore, because without arguments the command cannot be malformed
    }
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
   * on the specified {@code Scanner}. Unexpected command will be ignored up to {@code
   * MAX_UNEXPECTED_COUNT}** times, after which an exception will be thrown.
   *
   * @param scanner the {@code Scanner}.
   * @param expectedCommands the {@code ProtocolCommand}.
   * @return the {@code List<String>} of arguments. The list is empty (size equals zero) if there
   *     are no arguments.
   * @throws UnexpectedCommandException thrown when the number of unexpected commands exceeds {@code
   *     MAX_UNEXPECTED_COUNT}.
   */
  static List<String> expect(Scanner scanner, ProtocolCommand... expectedCommands)
      throws UnexpectedCommandException {
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
          argList = Arrays.asList(scanner.nextLine().trim().split(Protocol.SPACE));
        } catch (NoSuchElementException ignored) {
          argList = new ArrayList<>();
        }

        if (theProtocolCommand.isValidArgList(argList)) {
          return argList;
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
              argList.size() > 0 && argList.get(0) != null && argList.get(0).matches("^\\w{1,20}$");
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
  }

  /** The {@code Exception} for a command that is malformed for the protocol. */
  class MalformedCommandException extends Exception {
    public MalformedCommandException(String message) {
      super(message);
    }
  }

  /** The {@code Exception} for the {@code MAX_UNEXPECTED_COUNT}-th unexpected command. */
  class UnexpectedCommandException extends Exception {}
}
