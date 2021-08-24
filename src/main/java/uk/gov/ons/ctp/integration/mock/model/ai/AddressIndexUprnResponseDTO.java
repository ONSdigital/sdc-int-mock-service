package uk.gov.ons.ctp.integration.mock.model.ai;

import lombok.Data;

@Data
public class AddressIndexUprnResponseDTO {

  private AddressIndexUprnAddressDTO address;

  private String addressType;

  private String epoch;
}
