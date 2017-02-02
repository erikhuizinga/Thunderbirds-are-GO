package net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import net.Protocol.ClientCommands;
import net.Protocol.MalformedCommandException;
import net.Protocol.ServerCommands;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Created by erik.huizinga on 2-2-17. */
class ProtocolTest {

  @BeforeEach
  void setUp() {}

  @Test
  void testCommand() {
    try {
      assertEquals("PLAYER NAME", Protocol.validateAndFormatCommand(ClientCommands.PLAYER, "name"));
      assertEquals(
          "READY BLACK BARRYBADPAK 9",
          Protocol.validateAndFormatCommand(ServerCommands.READY, "black", "BarryBadpak", "9"));
    } catch (MalformedCommandException e) {
      e.printStackTrace();
      fail("an Exception was thrown where it shouldn't");
    }
    assertThrows(
        MalformedCommandException.class,
        () ->
            Protocol.validateAndFormatCommand(
                ClientCommands.PLAYER,
                "this name contains illegal characters (spaces) and is too long"));
    assertThrows(
        MalformedCommandException.class,
        () ->
            Protocol.validateAndFormatCommand(
                ServerCommands.READY,
                "gray", "somename", "19"));
  }
}
