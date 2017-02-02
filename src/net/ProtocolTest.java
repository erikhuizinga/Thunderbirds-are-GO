package net;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import net.Protocol.ClientCommand;
import net.Protocol.MalformedCommandException;
import net.Protocol.ServerCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/** Created by erik.huizinga on 2-2-17. */
class ProtocolTest {

  @BeforeEach
  void setUp() {}

  @Test
  void testValidateAndFormatCommand() {
    try {
      assertEquals("PLAYER NAME", Protocol.validateAndFormatCommand(ClientCommand.PLAYER, "name"));
      assertEquals(
          "READY BLACK BARRYBADPAK 9",
          Protocol.validateAndFormatCommand(ServerCommand.READY, "black", "BarryBadpak", "9"));
    } catch (MalformedCommandException e) {
      e.printStackTrace();
      fail("an Exception was thrown where it shouldn't");
    }
    assertThrows(
        MalformedCommandException.class,
        () ->
            Protocol.validateAndFormatCommand(
                ClientCommand.PLAYER,
                "this name contains illegal characters (spaces) and is too long"));
    assertThrows(
        MalformedCommandException.class,
        () -> Protocol.validateAndFormatCommand(ServerCommand.READY, "gray", "somename", "19"));
  }
}
