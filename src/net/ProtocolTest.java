package net;

import static net.Protocol.Keyword.CANCEL;
import static net.Protocol.Keyword.CHAT;
import static net.Protocol.Keyword.GO;
import static net.Protocol.Keyword.PLAYER;
import static net.Protocol.Keyword.READY;
import static net.Protocol.Keyword.WAITING;
import static net.Protocol.Keyword.WARNING;
import static net.Protocol.SPACE;
import static net.Protocol.expect;
import static net.Protocol.isValidDimension;
import static net.Protocol.validateAndFormatArgList;
import static net.Protocol.validateAndFormatCommandString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Scanner;
import net.Protocol.Executable.ExecutableNotSetException;
import net.Protocol.Keyword;
import net.Protocol.MalformedArgumentsException;
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
  void setUp() {
    EnumSet<Keyword> keywords = EnumSet.allOf(Keyword.class);
    for (Keyword keyword : keywords) {
      keyword.reset();
    }
  }

  @Test
  void testValidateAndFormatArgList() {
    // Test bad argument
    assertThrows(
        MalformedArgumentsException.class, () -> validateAndFormatArgList(PLAYER, badName));

    try {
      // Test one correct argument
      assertEquals(Collections.singletonList(name), validateAndFormatArgList(PLAYER, name));

      // Test more than one correct argument
      assertEquals(
          Arrays.asList(stone, name, dimensionString),
          validateAndFormatArgList(READY, stone, name, dimensionString));

      // Test ignoring of redundant arguments
      assertEquals(Collections.emptyList(), validateAndFormatArgList(WAITING));

    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
  }

  @Test
  void testValidateAndFormatCommandString() {
    try {
      assertEquals("PLAYER name", validateAndFormatCommandString(PLAYER, "name"));
      assertEquals(
          "READY black barrybadpak 9",
          validateAndFormatCommandString(READY, "black", "BarryBadpak", "9"));
    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
    assertThrows(
        MalformedArgumentsException.class,
        () ->
            validateAndFormatCommandString(
                PLAYER, "this name contains illegal characters (spaces) and is too long"));
    assertThrows(
        MalformedArgumentsException.class,
        () -> validateAndFormatCommandString(READY, "gray", "someName", "19"));

    // Test without arguments
    try {
      String theCommand = validateAndFormatCommandString(WAITING);
      assertEquals(WAITING.toString(), theCommand);

    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
  }

  @Test
  void testExpect() {
    String commandString;
    List<String> argList;
    try {
      // Test missing argument
      commandString = PLAYER.toString();
      scanner = new Scanner(commandString);
      assertThrows(MalformedArgumentsException.class, () -> expect(scanner, PLAYER));

      // Test malformed argument
      commandString = PLAYER.toString() + SPACE + badName;
      scanner = new Scanner(commandString);
      assertThrows(MalformedArgumentsException.class, () -> expect(scanner, PLAYER));

      // Test zero arguments
      commandString = validateAndFormatCommandString(WAITING);
      scanner = new Scanner(commandString);
      assertEquals(Collections.emptyList(), expect(scanner, WAITING).getArgList());

      // Test ignoring of redundant arguments
      commandString += SPACE + "we are arguments and we shouldn't be here :')";
      scanner = new Scanner(commandString);
      assertEquals(WAITING, expect(scanner, WAITING));
      scanner = new Scanner(commandString);
      assertEquals(WAITING.getArgList(), expect(scanner, WAITING).getArgList());

      // Test one argument
      commandString = validateAndFormatCommandString(PLAYER, name);
      scanner = new Scanner(commandString);
      argList = expect(scanner, PLAYER).getArgList();
      assertEquals(Collections.singletonList(name), argList);
      assertTrue(PLAYER.isValidArgList(argList));

      // Test more than one argument
      commandString = validateAndFormatCommandString(READY, stone, name, dimensionString);
      scanner = new Scanner(commandString);
      argList = expect(scanner, READY).getArgList();
      assertEquals(Arrays.asList(stone, name, dimensionString), argList);
      assertTrue(READY.isValidArgList(argList));

      // Test more than one command
      scanner = new Scanner(commandString);
      argList = expect(scanner, PLAYER, WAITING, READY).getArgList();
      assertTrue(READY.isValidArgList(argList));

    } catch (MalformedArgumentsException | UnexpectedKeywordException e) {
      failAllTheThings();
    }

    // Test unexpected keyword
    scanner = new Scanner(READY.toString());
    assertThrows(UnexpectedKeywordException.class, () -> expect(scanner, WAITING, PLAYER));
  }

  @Test
  void testPLAYER() {
    // Test correct arguments
    assertTrue(PLAYER.isValidArgList(Collections.singletonList(name)));

    // Test malformed argument
    assertFalse(PLAYER.isValidArgList(Collections.singletonList(badName)));

    // Test missing argument
    assertFalse(PLAYER.isValidArgList(Collections.emptyList()));
  }

  @Test
  void testGO() {
    // Test correct arguments
    assertTrue(GO.isValidArgList(Collections.singletonList(dimensionString)));
    assertTrue(GO.isValidArgList(Arrays.asList(dimensionString, name)));

    // Test malformed arguments
    assertFalse(GO.isValidArgList(Collections.singletonList(Integer.toString(3))));
    assertFalse(GO.isValidArgList(Arrays.asList(Integer.toString(3), name)));
    assertFalse(GO.isValidArgList(Collections.singletonList("thisIsNotAnInteger")));
    assertFalse(GO.isValidArgList(Arrays.asList(dimensionString, badName)));

    // Test missing argument
    assertFalse(GO.isValidArgList(Collections.emptyList()));
  }

  @Test
  void testWAITING() {
    // Test correct arguments
    assertTrue(WAITING.isValidArgList(Collections.emptyList()));

    // Test any arguments, which should be ignored
    assertTrue(WAITING.isValidArgList(Arrays.asList("a", "bc", "def")));
  }

  @Test
  void testCANCEL() {
    // Test correct arguments
    assertTrue(CANCEL.isValidArgList(Collections.emptyList()));

    // Test any arguments, which should be ignored
    assertTrue(CANCEL.isValidArgList(Arrays.asList("a", "bc", "def")));
  }

  @Test
  void testREADY() {
    // Test correct arguments
    assertTrue(READY.isValidArgList(Arrays.asList(stone, name, dimensionString)));

    // Test malformed argument
    assertFalse(READY.isValidArgList(Arrays.asList(stone, badName, dimensionString)));
    assertFalse(READY.isValidArgList(Arrays.asList(stone, name, "notANumber")));

    // Test missing arguments
    assertFalse(READY.isValidArgList(Collections.emptyList()));
    assertFalse(READY.isValidArgList(Collections.singletonList(stone)));
  }

  @Test
  void testCHAT() {
    // Test one argument
    assertTrue(CHAT.isValidArgList(Collections.singletonList("¡Hola!")));

    // Test missing argument
    assertFalse(CHAT.isValidArgList(Collections.emptyList()));

    // Test any number of arguments
    assertTrue(CHAT.isValidArgList(Arrays.asList("Hello,", "World!")));
    assertTrue(
        CHAT.isValidArgList(
            Collections.nCopies((int) (Math.random() * (Integer.MAX_VALUE - 1)) + 1, "chat?")));
  }

  @Test
  void testWARNING() {
    // Test one argument
    assertTrue(WARNING.isValidArgList(Collections.singletonList("¡Hola!")));

    // Test missing argument
    assertFalse(WARNING.isValidArgList(Collections.emptyList()));

    // Test any number of arguments
    assertTrue(WARNING.isValidArgList(Arrays.asList("Hello,", "World!")));
    assertTrue(
        WARNING.isValidArgList(
            Collections.nCopies((int) (Math.random() * (Integer.MAX_VALUE - 1)) + 1, "warn?")));
  }

  @Test
  void testIsValidDimension() {
    assertTrue(isValidDimension(5));
    assertTrue(isValidDimension(131));
    assertFalse(isValidDimension(6));
    assertFalse(isValidDimension(133));
    assertFalse(isValidDimension(3));
  }

  @Test
  void testKeywordSetArgList() {
    try {
      CHAT.setArgList(Collections.singletonList("OK"));
      CHAT.setArgList(Arrays.asList("OK", "as", "well"));
    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
    assertThrows(MalformedArgumentsException.class, () -> CHAT.setArgList(Collections.emptyList()));
  }

  @Test
  void testKeywordGetArgList() {
    assertThrows(MalformedArgumentsException.class, CHAT::getArgList);
    try {
      assertEquals(Collections.emptyList(), WAITING.getArgList());

      List<String> argList = Collections.singletonList("OK");
      List<String> otherArgList = Arrays.asList("Other", "args");

      CHAT.setArgList(otherArgList);
      assertNotEquals(argList, CHAT.getArgList());

      CHAT.setArgList(argList);
      assertEquals(argList, CHAT.getArgList());

    } catch (MalformedArgumentsException e) {
      failAllTheThings();
    }
  }

  @Test
  void testKeywordGetArgs() {
    List<String> argList = Arrays.asList("OK", "args");
    String[] args = argList.stream().toArray(String[]::new);
    List<String> otherArgList = Arrays.asList("Other", "args");

    try {
      CHAT.setArgList(otherArgList);
      assertNotEquals(args, CHAT.getArgs());

      CHAT.setArgList(argList);
      assertTrue(Arrays.equals(args, CHAT.getArgs()));

    } catch (MalformedArgumentsException e) {
      e.printStackTrace();
    }
  }

  @Test
  void testExecutableKeywords() {
    assertThrows(AssertionError.class, CHAT::execute);
    try {
      CHAT.setExecutable(list -> {});
      CHAT.execute();

      CHAT.setExecutable(System.out::println);
      CHAT.execute(Collections.singletonList("Hello!"));

      CHAT.setArgList(Arrays.asList("¡Hola,", "Mundo!"));
      CHAT.execute();

    } catch (ExecutableNotSetException | MalformedArgumentsException e) {
      failAllTheThings();
    }
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
