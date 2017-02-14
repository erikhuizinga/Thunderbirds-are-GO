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

import java.util.Arrays;
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
  private String dimensionString = Integer.toString(dimension);
  private String stone = Protocol.BLACK;

  @BeforeEach
  void setUp() {}

  @Test
  void testValidateAndFormatArgList() {
    ProtocolCommand playerCommand = ClientCommand.PLAYER;
    ProtocolCommand readyCommand = ServerCommand.READY;

    // Test bad argument
    assertThrows(
        MalformedArgumentsException.class, () -> validateAndFormatArgList(playerCommand, badName));

    try {
      // Test one correct argument
      assertEquals(Collections.singletonList(name), validateAndFormatArgList(playerCommand, name));

      // Test more than one correct argument
      assertEquals(
          Arrays.asList(stone, name, dimensionString),
          validateAndFormatArgList(readyCommand, stone, name, dimensionString));

      // Test ignoring of redundant arguments
      assertEquals(Collections.emptyList(), validateAndFormatArgList(ServerCommand.WAITING));

    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
  }

  @Test
  void testValidateAndFormatCommandString() {
    try {
      assertEquals("PLAYER name", validateAndFormatCommandString(ClientCommand.PLAYER, "name"));
      assertEquals(
          "READY black barrybadpak 9",
          validateAndFormatCommandString(ServerCommand.READY, "black", "BarryBadpak", "9"));
    } catch (MalformedArgumentsException e) {
      failAllTheThings();
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
      failAllTheThings();
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
      commandString = validateAndFormatCommandString(readyCommand, stone, name, dimensionString);
      scanner = new Scanner(commandString);
      argList = expect(scanner, readyCommand);
      assertTrue(readyCommand.isValidArgList(argList));

      // Test more than one command
      scanner = new Scanner(commandString);
      argList = expect(scanner, playerCommand, waitingCommand, readyCommand);
      assertTrue(readyCommand.isValidArgList(argList));

    } catch (MalformedArgumentsException | UnexpectedCommandException e) {
      failAllTheThings();
    }

    // Test unexpected command
    scanner = new Scanner(readyCommand.toString());
    assertThrows(
        UnexpectedCommandException.class, () -> expect(scanner, waitingCommand, playerCommand));
  }

  @Test
  void testPLAYER() {
    final ProtocolCommand command = ClientCommand.PLAYER;

    // Test correct arguments
    assertTrue(command.isValidArgList(Collections.singletonList(name)));

    // Test malformed argument
    assertFalse(command.isValidArgList(Collections.singletonList(badName)));

    // Test missing argument
    assertFalse(command.isValidArgList(Collections.emptyList()));
  }

  @Test
  void testGO() {
    final ProtocolCommand command = ClientCommand.GO;

    // Test correct arguments
    assertTrue(command.isValidArgList(Collections.singletonList(dimensionString)));

    // Test malformed argument
    assertFalse(command.isValidArgList(Collections.singletonList(Integer.toString(3))));

    // Test missing argument
    assertFalse(command.isValidArgList(Collections.emptyList()));
  }

  @Test
  void testWAITING() {
    // Test correct arguments
    assertTrue(ServerCommand.WAITING.isValidArgList(Collections.emptyList()));

    // Test any arguments, which should be ignored
    assertTrue(ServerCommand.WAITING.isValidArgList(Arrays.asList("a", "bc", "def")));
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

  private void failAllTheThings() {
    fail("fail all the things!");
  }
}
