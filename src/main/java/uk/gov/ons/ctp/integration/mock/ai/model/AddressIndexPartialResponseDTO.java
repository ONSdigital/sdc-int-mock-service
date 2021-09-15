package uk.gov.ons.ctp.integration.mock.ai.model;

import java.util.List;
import lombok.Data;

@Data
public class AddressIndexPartialResponseDTO {

  private String input;

  private List<AddressIndexPartialAddressDTO> addresses;

  private String filter;

  private boolean fallback;

  private boolean historical;

  private String epoch;

  private int limit;

  private int offset;

  private int total;

  private int maxScore;

  private boolean verbose;

  private String highlight;

  private boolean favourpaf;

  private boolean favourwelsh;

  private boolean includeauxiliarysearch;

  private int eboost;
  private int nboost;
  private int sboost;
  private int wboost;
}
