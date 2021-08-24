package uk.gov.ons.ctp.integration.mock.model.ai;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** This class holds data for the 'census' level of address data. */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressIndexAddressCensusDTO {
  private String addressType;

  private String estabType;

  private String countryCode;
}
