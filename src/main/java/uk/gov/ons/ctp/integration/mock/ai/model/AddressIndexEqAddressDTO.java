package uk.gov.ons.ctp.integration.mock.ai.model;

import lombok.Data;

@Data
public class AddressIndexEqAddressDTO {

  private String uprn;

  private String bestMatchAddress;

  private String bestMatchAddressType;
}
