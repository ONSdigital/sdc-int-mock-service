package uk.gov.ons.ctp.integration.mock.ai.model;

import java.util.List;
import lombok.Data;

@Data
public class AddressIndexRhPostcodeResponseDTO {

  private String postcode;

  private List<AddressIndexRhPostcodeAddressDTO> addresses;

  private String filter;

  private String epoch;

  private int limit;

  private int offset;

  private int total;

  private int maxScore;
}
