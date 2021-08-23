package uk.gov.ons.ctp.integration.mock.model.ai;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexPartialResultsDTO {

  private String apiVersion;

  private String dataVersion;

  private AddressIndexPartialResponseDTO response;

  private AddressIndexStatusDTO status;

  private ArrayList<String> errors;
}
