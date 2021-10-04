package uk.gov.ons.ctp.integration.mock.ai.model;

import lombok.Data;

@Data
public class AddressIndexEqPostcodeAddressDTO {

  private String postcode;

  private String streetName;

  private String townName;

  private int addressCount;

  private long firstUprn;

  private String postTown;
}
