package uk.gov.ons.ctp.integration.mock.config;

import lombok.Data;
import uk.gov.ons.ctp.common.rest.RestClientConfig;

@Data
public class AddressIndexConfig {
  private String token;
  private RestClientConfig restClientConfig;
}
