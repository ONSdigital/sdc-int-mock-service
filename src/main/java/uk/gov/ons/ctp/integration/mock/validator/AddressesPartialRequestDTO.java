package uk.gov.ons.ctp.integration.mock.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressesPartialRequestDTO {

  private boolean fallback = false;

  private int offset = 0;

  private int limit = 20;

  private String classificationfilter;

  private boolean historical = true;

  private boolean verbose = true;

  private String epoch;

  private String highlight = "true";

  private boolean favourpaf = true;
  private boolean favourwelsh = false;

  private int eboost = 1;
  private int nboost = 1;
  private int sboost = 1;
  private int wboost = 1;
}
