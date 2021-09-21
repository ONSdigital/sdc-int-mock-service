package uk.gov.ons.ctp.integration.mock.ai.model;

import lombok.Data;

@Data
public class AddressIndexUprnResponseDTO {

  private AddressIndexUprnAddressDTO address;

  private String addressType;

  private String epoch;
}
