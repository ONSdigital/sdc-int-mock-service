package uk.gov.ons.ctp.integration.mock.ai.model;

import java.util.List;
import lombok.Data;

@Data
public class AddressIndexEqPostcodeResponseDTO {

  private String partpostcode;

  private List<AddressIndexEqPostcodeAddressDTO> postcodes;
  
  private String filter;
  
  private boolean historical;

  private String epoch;

  private int limit;

  private int offset;

  private int total;

  private int maxScore;

  private boolean verbose;
}
