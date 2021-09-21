package uk.gov.ons.ctp.integration.mock.ai.model;

import java.util.ArrayList;
import lombok.Data;

@Data
public class AddressIndexUprnResultDTO {

  private String apiVersion;

  private String dataVersion;

  private AddressIndexUprnResponseDTO response;

  private AddressIndexStatusDTO status;

  private ArrayList<String> errors;
}
