package gov.nasa.pds.api.registry.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class LoggingAspectTest {

  @Test
  void sanitizeForLogLeavesNormalIdentifiersUnchanged() {
    String lidvid = "urn:nasa:pds:context:investigation:mission.mars2020::1.0";
    assertEquals(lidvid, LoggingAspect.sanitizeForLog(lidvid));
    assertEquals("", LoggingAspect.sanitizeForLog(""));
  }

  @Test
  void sanitizeForLogHandlesNull() {
    assertNull(LoggingAspect.sanitizeForLog(null));
  }

  @Test
  void sanitizeForLogReplacesLineBreaksAndTabs() {
    assertEquals("urn:nasa:pds:x__INFO fake entry_end",
        LoggingAspect.sanitizeForLog("urn:nasa:pds:x\r\nINFO fake entry\tend"));
  }

  @Test
  void sanitizeForLogReplacesOtherControlAndSeparatorCharacters() {
    // vertical tab, form feed, NUL, DEL, NEL (C1), line separator, paragraph separator
    assertEquals("a_b_c_d_e_f_g",
        LoggingAspect.sanitizeForLog("a\u000Bb\fc\u0000d\u007Fe\u0085f g"));
    assertEquals("a_b", LoggingAspect.sanitizeForLog("a b"));
  }

  @Test
  void sanitizeForLogRemovesAnsiEscapeSequences() {
    assertEquals("red text", LoggingAspect.sanitizeForLog("\u001B[31mred text\u001B[0m"));
  }
}
