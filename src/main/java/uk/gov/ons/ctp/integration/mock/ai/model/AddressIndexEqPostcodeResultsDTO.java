package uk.gov.ons.ctp.integration.mock.ai.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexEqPostcodeResultsDTO {

  private String apiVersion;

  private String dataVersion;

  private String termsAndConditions;

  private AddressIndexEqPostcodeResponseDTO response;
  
  private AddressIndexStatusDTO status;

  private ArrayList<String> errors;
}
