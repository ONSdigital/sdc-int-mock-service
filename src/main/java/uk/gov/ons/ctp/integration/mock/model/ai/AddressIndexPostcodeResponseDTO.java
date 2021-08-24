package uk.gov.ons.ctp.integration.mock.model.ai;

import java.util.List;
import lombok.Data;

@Data
public class AddressIndexPostcodeResponseDTO {

  private String postcode;

  private List<AddressIndexPostcodeAddressDTO> addresses;

  private String filter;

  private boolean historical;

  private String epoch;

  private int limit;

  private int offset;

  private int total;

  private int maxScore;

  private boolean verbose;

  private boolean includeauxiliarysearch;
}
