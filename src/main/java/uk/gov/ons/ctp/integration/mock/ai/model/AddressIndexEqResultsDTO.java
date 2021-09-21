package uk.gov.ons.ctp.integration.mock.ai.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexEqResultsDTO {

  private String apiVersion;

  private String dataVersion;

  private String termsAndConditions;

  private AddressIndexEqResponseDTO response;
  
  private AddressIndexStatusDTO status;

  private ArrayList<String> errors;
}
