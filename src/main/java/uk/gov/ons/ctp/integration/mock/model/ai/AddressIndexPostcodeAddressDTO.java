package uk.gov.ons.ctp.integration.mock.model.ai;

import lombok.Data;

/** Address Index query result splitting address into Census component fields */
@Data
public class AddressIndexPostcodeAddressDTO {

  private String uprn;

  private String parentUprn;

  private String formattedAddress;

  private String formattedAddressNag;

  private String formattedAddressPaf;

  private String formattedAddressNisra;

  private String welshFormattedAddressNag;

  private String welshFormattedAddressPaf;

  private String formattedAddressAuxiliary;

  private AddressIndexGeo geo;

  private String classificationCode;

  AddressIndexAddressCensusDTO census;

  private String lpiLogicalStatus;

  private long confidenceScore;

  private long underlyingScore;
}
