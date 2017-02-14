package net;

import static net.Protocol.SPACE;
import static net.Protocol.expect;
import static net.Protocol.isValidDimension;
import static net.Protocol.validateAndFormatArgList;
import static net.Protocol.validateAndFormatCommandString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

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
  private String badName = "thisNameIsABitTooLong";
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

    // Test without arguments
    try {
      String theCommand = validateAndFormatCommandString(ServerCommand.WAITING);
      assertEquals(ServerCommand.WAITING.toString(), theCommand);

    } catch (MalformedArgumentsException e) {
      fail("fail all the things!");
    }
  }

  @Test
  void testExpect() {
    final ProtocolCommand playerCommand = ClientCommand.PLAYER;
    final ProtocolCommand readyCommand = ServerCommand.READY;
    final ProtocolCommand waitingCommand = ServerCommand.WAITING;

    String commandString;
    List<String> argList;
    try {
      // Test missing argument
      commandString = playerCommand.toString();
      scanner = new Scanner(commandString);
      assertThrows(MalformedArgumentsException.class, () -> expect(scanner, playerCommand));

      // Test malformed argument
      commandString = playerCommand.toString() + SPACE + badName;
      scanner = new Scanner(commandString);
      assertThrows(MalformedArgumentsException.class, () -> expect(scanner, playerCommand));

      // Test zero arguments
      commandString = validateAndFormatCommandString(waitingCommand);
      scanner = new Scanner(commandString);
      assertEquals(Collections.emptyList(), expect(scanner, waitingCommand));

      // Test ignoring of redundant arguments
      commandString += SPACE + "we are arguments and we shouldn't be here :')";
      scanner = new Scanner(commandString);
      assertEquals(Collections.emptyList(), expect(scanner, waitingCommand));

      // Test one argument
      commandString = validateAndFormatCommandString(playerCommand, name);
      scanner = new Scanner(commandString);
      argList = expect(scanner, playerCommand);
      assertTrue(playerCommand.isValidArgList(argList));

      // Test more than one argument
      commandString =
          validateAndFormatCommandString(readyCommand, stone, name, Integer.toString(dimension));
      scanner = new Scanner(commandString);
      argList = expect(scanner, readyCommand);
      assertTrue(readyCommand.isValidArgList(argList));

      // Test more than one command
      scanner = new Scanner(commandString);
      argList = expect(scanner, playerCommand, waitingCommand, readyCommand);
      assertTrue(readyCommand.isValidArgList(argList));

    } catch (MalformedArgumentsException | UnexpectedCommandException e) {
      fail("fail all the things!");
    }

    // Test unexpected command
    scanner = new Scanner(readyCommand.toString());
    assertThrows(
        UnexpectedCommandException.class, () -> expect(scanner, waitingCommand, playerCommand));
  }

  @Test
  void testPLAYER() {
    final ProtocolCommand command = ClientCommand.PLAYER;
    String commandString = command.toString();
    String string = commandString + SPACE + name;

    // Test correct arguments
    assertTrue(command.isValidArgList(Collections.singletonList(name)));

    // Test malformed argument
    assertFalse(command.isValidArgList(Collections.singletonList(badName)));

    //TODO replace this with a test of validateAndFormatArgList
    assertThrows(
        MalformedArgumentsException.class, () -> validateAndFormatArgList(command, badName));
  }

  @Test
  void testWAITING() {
    final ProtocolCommand command = ServerCommand.WAITING;
    String string = command.toString();

    // Test correct arguments
    assertTrue(command.isValidArgList(Collections.emptyList()));

    /* Incorrect arguments are not possible: any should be ignored and this is tested by testValidateAndFormatArgList */

    // Test parsing of incoming communication
    try {
      // No arguments
      scanner = new Scanner(string);
      assertEquals(Collections.emptyList(), expect(scanner, command));

      // Any arguments should not be returned
      scanner = new Scanner(string + SPACE + "argue @ll th3 things!");
      assertEquals(Collections.emptyList(), expect(scanner, command));

    } catch (UnexpectedCommandException | MalformedArgumentsException e) {
      fail("fail all the things!");
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
