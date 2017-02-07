package net;

import static net.Protocol.SPACE;
import static net.Protocol.expect;
import static net.Protocol.validateAndFormatCommand;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import net.Protocol.ClientCommand;
import net.Protocol.MalformedCommandException;
import net.Protocol.ProtocolCommand;
import net.Protocol.ServerCommand;
import net.Protocol.UnexpectedCommandException;
import org.junit.jupiter.api.AfterEach;
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
    ProtocolCommand command = ClientCommand.PLAYER;
    String name = "BarryBadpak";

    String commandString;
    List<String> argList;
    try {
      // Test absent argument
      commandString = command.toString();
      scanner = new Scanner(commandString);
      assertThrows(UnexpectedCommandException.class, () -> expect(scanner, command));

      // Test malformed argument
      String badName = "thisNameIsABitTooLong";
      commandString = command.toString() + SPACE + badName;
      scanner = new Scanner(commandString);
      assertThrows(UnexpectedCommandException.class, () -> expect(scanner, command));
      assertFalse(command.isValidArgList(Collections.singletonList(badName)));

      commandString = validateAndFormatCommand(command, name);
      scanner = new Scanner(commandString);
      argList = expect(scanner, command);
      assertTrue(command.isValidArgList(argList));

    } catch (MalformedCommandException | UnexpectedCommandException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testWAITING() {
    ProtocolCommand command = ServerCommand.WAITING;
    String string = command.toString();

    // Test formatting of outgoing communication
    String theCommand = validateAndFormatCommand(command);
    assertEquals(string, theCommand);

    // Test parsing of incoming communication
    scanner = new Scanner(string);
    try {
      // No arguments
      assertEquals(new ArrayList<String>(), expect(scanner, command));

      // Any arguments should be left untouched
      scanner = new Scanner(string + SPACE + "argue @ll th3 things!");
      assertEquals(Arrays.asList("argue", "@ll", "th3", "things!"), expect(scanner, command));

    } catch (UnexpectedCommandException e) {
      e.printStackTrace();
    }
  }

  @AfterEach
  void tearDown() {
    if (scanner != null) {
      scanner.close();
    }
  }
}
