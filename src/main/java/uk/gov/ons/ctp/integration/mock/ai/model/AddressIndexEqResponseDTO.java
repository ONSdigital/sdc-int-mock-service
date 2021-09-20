package uk.gov.ons.ctp.integration.mock.ai.model;

import java.util.List;
import lombok.Data;

@Data
public class AddressIndexEqResponseDTO {

  private String input;

  private List<AddressIndexEqAddressDTO> addresses;
  
  private String filter;
  
  private boolean fallback;

  private String epoch;

  private int limit;

  private int offset;

  private int total;

  private int maxScore;

  private boolean favourpaf;

  private boolean favourwelsh;

  private int eboost;
  private int nboost;
  private int sboost;
  private int wboost;
}
