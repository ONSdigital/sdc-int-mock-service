package uk.gov.ons.ctp.integration.mock.model.ai;

import lombok.Data;

/** Holds 'status' data for top level AI results. */
@Data
public class AddressIndexStatusDTO {

  private int code;

  private String message;
}
