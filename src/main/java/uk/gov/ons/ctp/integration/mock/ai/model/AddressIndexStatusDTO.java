package uk.gov.ons.ctp.integration.mock.ai.model;

import lombok.Data;

/** Holds 'status' data for top level AI results. */
@Data
public class AddressIndexStatusDTO {

  private int code;

  private String message;
}
