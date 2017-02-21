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
import net.Protocol.ClientKeywords;
import net.Protocol.GeneralKeywords;
import net.Protocol.Keyword;
import net.Protocol.MalformedArgumentsException;
import net.Protocol.ServerKeywords;
import net.Protocol.UnexpectedKeywordException;
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
    Keyword playerKeyword = ClientKeywords.PLAYER;
    Keyword readyKeyword = ServerKeywords.READY;

    // Test bad argument
    assertThrows(
        MalformedArgumentsException.class, () -> validateAndFormatArgList(playerKeyword, badName));

    try {
      // Test one correct argument
      assertEquals(Collections.singletonList(name), validateAndFormatArgList(playerKeyword, name));

      // Test more than one correct argument
      assertEquals(
          Arrays.asList(stone, name, dimensionString),
          validateAndFormatArgList(readyKeyword, stone, name, dimensionString));

      // Test ignoring of redundant arguments
      assertEquals(Collections.emptyList(), validateAndFormatArgList(ServerKeywords.WAITING));

    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
  }

  @Test
  void testValidateAndFormatCommandString() {
    try {
      assertEquals("PLAYER name", validateAndFormatCommandString(ClientKeywords.PLAYER, "name"));
      assertEquals(
          "READY black barrybadpak 9",
          validateAndFormatCommandString(ServerKeywords.READY, "black", "BarryBadpak", "9"));
    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
    assertThrows(
        MalformedArgumentsException.class,
        () ->
            validateAndFormatCommandString(
                ClientKeywords.PLAYER,
                "this name contains illegal characters (spaces) and is too long"));
    assertThrows(
        MalformedArgumentsException.class,
        () -> validateAndFormatCommandString(ServerKeywords.READY, "gray", "someName", "19"));

    // Test without arguments
    try {
      String theCommand = validateAndFormatCommandString(ServerKeywords.WAITING);
      assertEquals(ServerKeywords.WAITING.toString(), theCommand);

    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
  }

  @Test
  void testExpect() {
    final Keyword playerKeyword = ClientKeywords.PLAYER;
    final Keyword readyKeyword = ServerKeywords.READY;
    final Keyword waitingKeyword = ServerKeywords.WAITING;

    String keywordString;
    List<String> command;
    try {
      // Test missing argument
      keywordString = playerKeyword.toString();
      scanner = new Scanner(keywordString);
      assertThrows(MalformedArgumentsException.class, () -> expect(scanner, playerKeyword));

      // Test malformed argument
      keywordString = playerKeyword.toString() + SPACE + badName;
      scanner = new Scanner(keywordString);
      assertThrows(MalformedArgumentsException.class, () -> expect(scanner, playerKeyword));

      // Test zero arguments
      keywordString = validateAndFormatCommandString(waitingKeyword);
      scanner = new Scanner(keywordString);
      assertEquals(
          Collections.singletonList(waitingKeyword.toString()), expect(scanner, waitingKeyword));

      // Test ignoring of redundant arguments
      keywordString += SPACE + "we are arguments and we shouldn't be here :')";
      scanner = new Scanner(keywordString);
      assertEquals(
          Collections.singletonList(waitingKeyword.toString()), expect(scanner, waitingKeyword));

      // Test one argument
      keywordString = validateAndFormatCommandString(playerKeyword, name);
      scanner = new Scanner(keywordString);
      command = expect(scanner, playerKeyword);
      assertEquals(Arrays.asList(playerKeyword.toString(), name), command);
      assertTrue(playerKeyword.isValidArgList(command.subList(1, command.size())));

      // Test more than one argument
      keywordString = validateAndFormatCommandString(readyKeyword, stone, name, dimensionString);
      scanner = new Scanner(keywordString);
      command = expect(scanner, readyKeyword);
      assertEquals(Arrays.asList(readyKeyword.toString(), stone, name, dimensionString), command);
      assertTrue(readyKeyword.isValidArgList(command.subList(1, command.size())));

      // Test more than one command
      scanner = new Scanner(keywordString);
      command = expect(scanner, playerKeyword, waitingKeyword, readyKeyword);
      assertTrue(readyKeyword.isValidArgList(command.subList(1, command.size())));

    } catch (MalformedArgumentsException | UnexpectedKeywordException e) {
      failAllTheThings();
    }

    // Test unexpected keyword
    scanner = new Scanner(readyKeyword.toString());
    assertThrows(
        UnexpectedKeywordException.class, () -> expect(scanner, waitingKeyword, playerKeyword));
  }

  @Test
  void testPLAYER() {
    final Keyword keyword = ClientKeywords.PLAYER;

    // Test correct arguments
    assertTrue(keyword.isValidArgList(Collections.singletonList(name)));

    // Test malformed argument
    assertFalse(keyword.isValidArgList(Collections.singletonList(badName)));

    // Test missing argument
    assertFalse(keyword.isValidArgList(Collections.emptyList()));
  }

  @Test
  void testGO() {
    final Keyword keyword = ClientKeywords.GO;

    // Test correct arguments
    assertTrue(keyword.isValidArgList(Collections.singletonList(dimensionString)));
    assertTrue(keyword.isValidArgList(Arrays.asList(dimensionString, name)));

    // Test malformed arguments
    assertFalse(keyword.isValidArgList(Collections.singletonList(Integer.toString(3))));
    assertFalse(keyword.isValidArgList(Collections.singletonList("thisIsNotAnInteger")));
    assertFalse(keyword.isValidArgList(Arrays.asList(dimensionString, badName)));

    // Test missing argument
    assertFalse(keyword.isValidArgList(Collections.emptyList()));
  }

  @Test
  void testWAITING() {
    final Keyword keyword = ServerKeywords.WAITING;

    // Test correct arguments
    assertTrue(keyword.isValidArgList(Collections.emptyList()));

    // Test any arguments, which should be ignored
    assertTrue(keyword.isValidArgList(Arrays.asList("a", "bc", "def")));
  }

  @Test
  void testCANCEL() {
    final Keyword keyword = ClientKeywords.CANCEL;

    // Test correct arguments
    assertTrue(keyword.isValidArgList(Collections.emptyList()));

    // Test any arguments, which should be ignored
    assertTrue(keyword.isValidArgList(Arrays.asList("a", "bc", "def")));
  }

  @Test
  void testREADY() {
    final Keyword keyword = ServerKeywords.READY;

    // Test correct arguments
    assertTrue(keyword.isValidArgList(Arrays.asList(stone, name, dimensionString)));

    // Test malformed argument
    assertFalse(keyword.isValidArgList(Arrays.asList(stone, badName, dimensionString)));
    assertFalse(keyword.isValidArgList(Arrays.asList(stone, name, "notANumber")));

    // Test missing arguments
    assertFalse(keyword.isValidArgList(Collections.emptyList()));
    assertFalse(keyword.isValidArgList(Collections.singletonList(stone)));
  }

  @Test
  void testCHAT() {
    final Keyword keyword = GeneralKeywords.CHAT;

    // Test one argument
    assertTrue(keyword.isValidArgList(Collections.singletonList("hi!")));

    // Test missing argument
    assertFalse(keyword.isValidArgList(Collections.emptyList()));

    // Test any number of arguments
    assertTrue(keyword.isValidArgList(Arrays.asList("Hello,", "World!")));
    assertTrue(
        keyword.isValidArgList(
            Collections.nCopies((int) (Math.random() * (Integer.MAX_VALUE - 1)) + 1, "chat?")));
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
