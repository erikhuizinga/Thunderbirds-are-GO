package net;

import static net.Protocol.SPACE;
import static net.Protocol.expect;
import static net.Protocol.validateAndFormatCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import net.Protocol.ClientCommand;
import net.Protocol.MalformedCommandException;
import net.Protocol.ProtocolCommand;
import net.Protocol.ServerCommand;
import net.Protocol.UnexpectedCommandException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Created by erik.huizinga on 2-2-17. */
class ProtocolTest {

  Scanner scanner;

  @BeforeEach
  void setUp() {}

  @Test
  void testValidateAndFormatCommand() {
    try {
      assertEquals("PLAYER NAME", validateAndFormatCommand(ClientCommand.PLAYER, "name"));
      assertEquals(
          "READY BLACK BARRYBADPAK 9",
          validateAndFormatCommand(ServerCommand.READY, "black", "BarryBadpak", "9"));
    } catch (MalformedCommandException e) {
      e.printStackTrace();
      fail("an Exception was thrown where it shouldn't");
    }
    assertThrows(
        MalformedCommandException.class,
        () ->
            validateAndFormatCommand(
                ClientCommand.PLAYER,
                "this name contains illegal characters (spaces) and is too long"));
    assertThrows(
        MalformedCommandException.class,
        () -> validateAndFormatCommand(ServerCommand.READY, "gray", "somename", "19"));
  }

  @Test
  void testExpect() {
    ProtocolCommand playerCommand = ClientCommand.PLAYER;
    String name = "BarryBadpak";

    String commandString;
    List<String> argList;
    try {
      commandString = playerCommand.toString();
      scanner = new Scanner(commandString);
      assertThrows(UnexpectedCommandException.class, () -> expect(scanner, playerCommand));

      commandString = playerCommand.toString() + SPACE + "thisNameIsABitTooLong";
      scanner = new Scanner(commandString);
      assertThrows(UnexpectedCommandException.class, () -> expect(scanner, playerCommand));

      commandString = validateAndFormatCommand(playerCommand, name);
      scanner = new Scanner(commandString);
      argList = expect(scanner, playerCommand);
      assertTrue(playerCommand.isValidArgList(argList));

    } catch (MalformedCommandException | UnexpectedCommandException e) {
      e.printStackTrace();

    } finally {
      scanner.close();
    }
  }

  @Test
  void testWAITING() {
    ProtocolCommand command = ServerCommand.WAITING;
    String string = command.toString();

    // Test formatting for sending
    String theCommand = validateAndFormatCommand(command);
    assertEquals(string, theCommand);

    // Test parsing for receiving
    scanner = new Scanner(string);
    try {
      assertEquals(new ArrayList<String>(), expect(scanner, command));

    } catch (UnexpectedCommandException e) {
      e.printStackTrace();
    }
  }
}
