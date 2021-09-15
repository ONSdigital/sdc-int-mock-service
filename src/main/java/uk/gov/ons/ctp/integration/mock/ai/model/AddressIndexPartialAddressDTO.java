package uk.gov.ons.ctp.integration.mock.ai.model;

import lombok.Data;

@Data
public class AddressIndexPartialAddressDTO {

  private String uprn;

  private String parentUprn;

  private String formattedAddress;

  private String formattedAddressNag;

  private String formattedAddressPaf;

  private String formattedAddressNisra;

  private String welshFormattedAddressNag;

  private String welshFormattedAddressPaf;

  private String formattedAddressAuxiliary;

  private AddressIndexHighlights highlights;

  private AddressIndexGeo geo;

  private String classificationCode;

  AddressIndexAddressCensusDTO census;

  private String lpiLogicalStatus;

  private long confidenceScore;

  private long underlyingScore;
}
