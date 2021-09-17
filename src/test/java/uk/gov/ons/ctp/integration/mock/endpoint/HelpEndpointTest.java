package uk.gov.ons.ctp.integration.mock.endpoint;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class HelpEndpointTest {
  HelpEndpoint controller = new HelpEndpoint();

  @Test
  public void shouldOnlyCountAddressTypes() {
    assertThrows(
        IllegalArgumentException.class, () -> controller.count("anything", RequestType.CASE_ID));
  }

  @Test
  public void shouldCountRhPostcodeResponse() throws Exception {
    assertEquals(40, controller.count("cf32tw", RequestType.AI_RH_POSTCODE));
  }

  @Test
  public void shouldCountPostcodeResponse() throws Exception {
    assertEquals(21, controller.count("cf51ad", RequestType.AI_POSTCODE));
  }

  @Test
  public void shouldCountPartialResponse() throws Exception {
    assertEquals(20, controller.count("okehampton-road", RequestType.AI_PARTIAL));
  }

  @Test
  public void shouldCountEqResponse() throws Exception {
    assertEquals(19, controller.count("ex24l", RequestType.AI_EQ));
  }

  @Test
  public void shouldCountUprnResponse() throws Exception {
    assertEquals(1, controller.count("100040239948", RequestType.AI_RH_UPRN));
  }
}
