package uk.gov.ons.ctp.integration.mock.model.ai;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexRhPostcodeResultsDTO {

  private String apiVersion;

  private String dataVersion;

  private AddressIndexRhPostcodeResponseDTO response;

  private AddressIndexStatusDTO status;

  private ArrayList<String> errors;
}
