package uk.gov.ons.ctp.integration.mock.ai.model;

import lombok.Data;

@Data
public class AddressIndexUprnAddressDTO {

  private String uprn;

  private String formattedAddress;

  private String addressLine1;

  private String addressLine2;

  private String addressLine3;

  private String townName;

  private String postcode;

  private String foundAddressType;

  private String censusAddressType;

  private String censusEstabType;

  private String countryCode;

  private String organisationName;
}
