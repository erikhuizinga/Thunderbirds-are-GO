package net;

import static net.Protocol.SPACE;
import static net.Protocol.expect;
import static net.Protocol.isValidDimension;
import static net.Protocol.validateAndFormatCommandString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import net.Protocol.ClientCommand;
import net.Protocol.MalformedArgumentsException;
import net.Protocol.ProtocolCommand;
import net.Protocol.ServerCommand;
import net.Protocol.UnexpectedCommandException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Created by erik.huizinga on 2-2-17. */
class ProtocolTest {

  private Scanner scanner;
  private String name = "barrybadpak";
  private int dimension = 19;
  private String stone = Protocol.BLACK;

  @BeforeEach
  void setUp() {}

  @Test
  void testValidateAndFormatCommandString() {
    try {
      assertEquals("PLAYER name", validateAndFormatCommandString(ClientCommand.PLAYER, "name"));
      assertEquals(
          "READY black barrybadpak 9",
          validateAndFormatCommandString(ServerCommand.READY, "black", "BarryBadpak", "9"));
    } catch (MalformedArgumentsException e) {
      e.printStackTrace();
      fail("fail all the things!");
    }
    assertThrows(
        MalformedArgumentsException.class,
        () ->
            validateAndFormatCommandString(
                ClientCommand.PLAYER,
                "this name contains illegal characters (spaces) and is too long"));
    assertThrows(
        MalformedArgumentsException.class,
        () -> validateAndFormatCommandString(ServerCommand.READY, "gray", "someName", "19"));

    // Test the method without arguments
    String theCommand = null;
    try {
      theCommand = validateAndFormatCommandString(ServerCommand.WAITING);
    } catch (MalformedArgumentsException e) {
      e.printStackTrace();
      fail("fail all the things!");
    }
    assertEquals(ServerCommand.WAITING.toString(), theCommand);
  }

  @Test
  void testExpect() {
    //TODO test with more than one argument
    //TODO test with more than one command
    final ProtocolCommand playerCommand = ClientCommand.PLAYER;

    String commandString;
    List<String> argList;
    try {
      // Test absent argument
      commandString = playerCommand.toString();
      scanner = new Scanner(commandString);
      assertThrows(UnexpectedCommandException.class, () -> expect(scanner, playerCommand));

      // Test malformed argument
      String badName = "thisNameIsABitTooLong";
      commandString = playerCommand.toString() + SPACE + badName;
      scanner = new Scanner(commandString);
      assertThrows(UnexpectedCommandException.class, () -> expect(scanner, playerCommand));
      assertFalse(playerCommand.isValidArgList(Collections.singletonList(badName)));

      commandString = validateAndFormatCommandString(playerCommand, name);
      scanner = new Scanner(commandString);
      argList = expect(scanner, playerCommand);
      assertTrue(playerCommand.isValidArgList(argList));

      final ProtocolCommand waitingCommand = ServerCommand.WAITING;
      commandString = validateAndFormatCommandString(waitingCommand);
      scanner = new Scanner(commandString);
      assertEquals(new ArrayList<String>(), expect(scanner, waitingCommand));

      commandString += SPACE + "we are arguments and we shouldn't be here :')";
      scanner = new Scanner(commandString);
      assertEquals(new ArrayList<String>(), expect(scanner, waitingCommand));

    } catch (MalformedArgumentsException | UnexpectedCommandException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testWAITING() {
    ProtocolCommand command = ServerCommand.WAITING;
    String string = command.toString();

    // Test formatting of outgoing communication
    String theCommand = null;
    try {
      theCommand = validateAndFormatCommandString(command);
    } catch (MalformedArgumentsException e) {
      fail("fail all the things!");
    }
    assertEquals(string, theCommand);

    // Test parsing of incoming communication
    scanner = new Scanner(string);
    try {
      // No arguments
      assertEquals(new ArrayList<String>(), expect(scanner, command));

      // Any arguments should not be returned
      scanner = new Scanner(string + SPACE + "argue @ll th3 things!");
      assertEquals(new ArrayList<String>(), expect(scanner, command));

    } catch (UnexpectedCommandException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testIsValidDimension() {
    assertTrue(isValidDimension(5));
    assertTrue(isValidDimension(131));
    assertFalse(isValidDimension(6));
    assertFalse(isValidDimension(133));
    assertFalse(isValidDimension(3));
  }

  @AfterEach
  void tearDown() {
    if (scanner != null) {
      scanner.close();
    }
  }
}
