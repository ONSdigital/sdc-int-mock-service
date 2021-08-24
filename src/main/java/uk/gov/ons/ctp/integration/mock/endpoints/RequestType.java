package uk.gov.ons.ctp.integration.mock.endpoints;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexUprnResultDTO;

@Getter
public enum RequestType {
  AI_RH_POSTCODE(
      "/addresses/rh/postcode/{postcode}",
      "Search for an address by postcode. RH version.",
      HttpStatus.OK,
      "POSTCODE",
      AddressIndexRhPostcodeResultsDTO.class,
      "offset",
      "limit"),
  AI_PARTIAL(
      "/addresses/partial",
      "Search by partial address.",
      HttpStatus.OK,
      "INPUT",
      AddressIndexPartialResultsDTO.class,
      "input",
      "offset",
      "limit"),
  AI_POSTCODE(
      "/addresses/postcode/{postcode}",
      "Search for an address by postcode.",
      HttpStatus.OK,
      "POSTCODE",
      AddressIndexPostcodeResultsDTO.class,
      "offset",
      "limit"),
  AI_RH_UPRN(
      "/addresses/rh/uprn/{uprn}",
      "Gets an address by UPRN.",
      HttpStatus.NOT_FOUND,
      null,
      AddressIndexUprnResultDTO.class),
  AI_EQ(
      "/addresses/eq",
      "Search for address for type ahead.",
      HttpStatus.OK,
      "INPUT",
      String.class,
      "input");

  private String url;
  private String description;
  private String path;
  private HttpStatus notFoundHttpStatus;
  private String placeholderName; // For not-found response text
  private Class<?> responseClass;
  private String[] queryParams;

  private RequestType(
      String url,
      String description,
      HttpStatus notFoundHttpStatus,
      String placeholderName,
      Class<?> responseClass,
      String... queryParams) {
    this.url = url;
    this.description = description;
    this.notFoundHttpStatus = notFoundHttpStatus;
    this.placeholderName = placeholderName;
    this.path = url.replaceAll("/\\{.*\\}", "");
    this.responseClass = responseClass;
    this.queryParams = queryParams;
  }
}
