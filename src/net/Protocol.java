package net;

import java.util.LinkedList;
import java.util.List;

/** Created by erik.huizinga on 2-2-17. */
public class Protocol {

  public static final String SPACE = " ";
  public static final String BLACK = "BLACK";
  public static final String WHITE = "WHITE";

  /**
   * Validate and format the specified {@code ProtocolCommand} with the specified arguments.
   *
   * @param protocolCommand the {@code ProtocolCommand}.
   * @param args the arguments, a comma-separated list of {@code String} arguments.
   * @return the command as a {@code String}, ready to send over a {@code Peer} to {@code Peer}
   *     connection.
   * @throws MalformedCommandException thrown if the specified arguments are not conform the
   *     protocol.
   */
  public static String validateAndFormatCommand(ProtocolCommand protocolCommand, String... args)
      throws MalformedCommandException {
    String result = protocolCommand.toString();
    String arg;
    List<String> argList = new LinkedList<>();
    for (int i = 0; i < args.length; i++) {
      arg = args[i].toUpperCase();
      argList.add(arg);
      result += SPACE + arg;
    }
    if (!protocolCommand.isValidArgList(argList)) {
      throw new MalformedCommandException("command malformed by protocol: " + result);
    }
    return result;
  }

  /**
   * Check if the specified dimension is valid conform protocol.
   *
   * @param dimension the dimension.
   * @return {@code true} if valid; {@code false} otherwise.
   */
  public static boolean isValidDimension(int dimension) {
    return dimension >= 5 && dimension <= 131 && dimension % 2 != 0;
  }

  /** The {@code Client} protocol commands. */
  public enum ClientCommand implements ProtocolCommand {
    PLAYER,
    GO;

    @Override
    public boolean isValidArgList(List<String> argList) {
      boolean isValid;
      switch (this) {
        case PLAYER:
          isValid =
              argList.size() > 0 && argList.get(0) != null && argList.get(0).matches("^\\w{1,20}$");
          break;

        case GO:
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
  public enum ServerCommand implements ProtocolCommand {
    WAITING,
    READY;

    @Override
    public boolean isValidArgList(List<String> argList) {
      switch (this) {
        case WAITING:
          return true; // No arguments, ignore any
        case READY:
          return argList.size() == 3
              && (argList.get(0).equals(BLACK) || argList.get(0).equals(WHITE))
              && ClientCommand.PLAYER.isValidArgList(
                  argList.subList(1, 2)); // TODO && board size valid?
        default:
          return false;
      }
    }
  }

  /** The general protocol commands for {@code Client}-{@code Server} communication. */
  public enum Command implements ProtocolCommand {
    CHAT;

    @Override
    public boolean isValidArgList(List<String> argList) {
      return false;
    }
  }

  /** The interface for every type of {@code ProtocolCommand}. */
  public interface ProtocolCommand {

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
  public static class MalformedCommandException extends Exception {
    public MalformedCommandException(String message) {
      super(message);
    }
  }
}
