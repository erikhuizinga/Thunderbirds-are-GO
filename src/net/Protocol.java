package net;

import java.util.LinkedList;
import java.util.List;

/** Created by erik.huizinga on 2-2-17. */
public class Protocol {

  public static final String SPACE = " ";
  public static final String BLACK = "BLACK";
  public static final String WHITE = "WHITE";

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
    READY;

    @Override
    public boolean isValidArgList(List<String> argList) {
      switch (this) {
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

  public interface ProtocolCommand {
    boolean isValidArgList(List<String> argList);
  }

  public static class MalformedCommandException extends Exception {
    public MalformedCommandException(String message) {
      super(message);
    }
  }
}