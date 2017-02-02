package net;

/** Created by erik.huizinga on 2-2-17. */
public class Protocol {

  public static final String SPACE = " ";
  public static final String BLACK = "BLACK";
  public static final String WHITE = "WHITE";

  public static String validateAndFormatCommand(Command command, String... keys)
      throws MalformedCommandException {
    String result = command.toString();
    for (int i = 0; i < keys.length; i++) {
      keys[i] = keys[i].toUpperCase();
      result += SPACE + keys[i];
    }
    if (!command.isValidKey(keys)) {
      throw new MalformedCommandException("command malformed by protocol: " + result);
    }
    return result;
  }

  /** The {@code Client} protocol commands. */
  public enum ClientCommands implements Command {
    PLAYER,
    GO;

    @Override
    public boolean isValidKey(String... keys) {
      boolean isValid;
      switch (this) {
        case PLAYER:
          isValid = keys[0] != null && keys[0].matches("^\\w{1,20}$");
          break;

        case GO:
          try {
            int dim = Integer.parseInt(keys[0]);
            isValid = keys[0] != null && dim >= 5 && dim <= 131 && dim % 2 != 0;
          } catch (NumberFormatException e) {
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
  public enum ServerCommands implements Command {
    READY;

    @Override
    public boolean isValidKey(String... keys) {
      switch (this) {
        case READY:
          return keys.length == 3
              && (keys[0].equals(BLACK) || keys[0].equals(WHITE))
              && ClientCommands.PLAYER.isValidKey(keys[1]); // TODO && board size valid?
        default:
          return false;
      }
    }
  }

  /** The general protocol commands for {@code Client}-{@code Server} communication. */
  public enum Commands implements Command {
    CHAT;

    @Override
    public boolean isValidKey(String... keys) {
      return false;
    }
  }

  private interface Command {
    boolean isValidKey(String... keys);
  }

  public static class MalformedCommandException extends Exception {
    public MalformedCommandException(String message) {
      super(message);
    }
  }
}
