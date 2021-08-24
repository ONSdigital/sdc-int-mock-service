package uk.gov.ons.ctp.integration.mock.validator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AddressesRhPostcodeRequestDTO {

  private int offset = 0;

  private int limit = 100;

  private String classificationfilter;

  private boolean historical = true;

  private boolean verbose = true;

  private boolean favourpaf = true;
  private boolean favourwelsh = false;

  private String epoch;
}
