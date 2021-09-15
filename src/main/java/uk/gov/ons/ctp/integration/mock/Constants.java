package uk.gov.ons.ctp.integration.mock;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
  public static final String NO_DATA_FILE_NAME = "_notFound";

  // This is the name of an optional file which may live in the captured data directory.
  // If present it contains strings which are used in the /help endpoint to describe particular data
  // files.
  public static final String INVENTORY_FILE_NAME = "_inventory.properties";

  public static final String INTERNAL_FILE_NAME_PREFIX = "_";

  public static final int CAPTURE_MAXIMUM_RESULTS = 1000;
}
