package uk.gov.ons.ctp.integration.mock.endpoint;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.RmCaseDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexEqPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexEqResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexUprnResultDTO;

@Getter
public enum RequestType {
  AI_RH_POSTCODE(
      "/addresses/rh/postcode/{postcode}",
      "/addresses/rh/postcode",
      "Search for an address by postcode. RH version.",
      HttpStatus.OK,
      "POSTCODE",
      AddressIndexRhPostcodeResultsDTO.class,
      "offset",
      "limit"),
  AI_PARTIAL(
      "/addresses/partial",
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
      "/addresses/postcode",
      "Search for an address by postcode.",
      HttpStatus.OK,
      "POSTCODE",
      AddressIndexPostcodeResultsDTO.class,
      "offset",
      "limit"),
  AI_RH_UPRN(
      "/addresses/rh/uprn/{uprn}",
      "/addresses/rh/uprn",
      "Gets an address by UPRN.",
      HttpStatus.NOT_FOUND,
      null,
      AddressIndexUprnResultDTO.class),
  AI_EQ(
      "/addresses/eq",
      "/addresses/eq",
      "Search for address for type ahead.",
      HttpStatus.OK,
      "INPUT",
      AddressIndexEqResultsDTO.class,
      "input"),
  AI_EQ_POSTCODE(
      "/addresses/eq",
      "/addresses/eq",
      "Search for address for type ahead.",
      HttpStatus.OK,
      "INPUT",
      AddressIndexEqPostcodeResultsDTO.class,
      "input"),
  CASE_REF(
      "/cases/ref/{ref}",
      "/cases/caseref",
      "Search for caseRef for cases.",
      HttpStatus.NOT_FOUND,
      null,
      RmCaseDTO.class,
      "caseEvents"),
  CASE_QID(
      "/cases/{caseId}/qid",
      "/cases/questionnaires",
      "Search for questionnaires for cases.",
      HttpStatus.NOT_FOUND,
      null,
      String.class,
      "individual",
      "individualCaseId"),
  CASE_ID(
      "/cases/{caseId}",
      "/cases/caseid",
      "Search for questionnaires for cases.",
      HttpStatus.NOT_FOUND,
      null,
      RmCaseDTO.class,
      "caseEvents");

  private String url;
  private String description;
  private String path;
  private HttpStatus notFoundHttpStatus;
  private String placeholderName; // For not-found response text
  private Class<?> responseClass;
  private String[] queryParams;

  private RequestType(
      String url,
      String path,
      String description,
      HttpStatus notFoundHttpStatus,
      String placeholderName,
      Class<?> responseClass,
      String... queryParams) {
    this.url = url;
    this.path = path;
    this.description = description;
    this.notFoundHttpStatus = notFoundHttpStatus;
    this.placeholderName = placeholderName;
    this.responseClass = responseClass;
    this.queryParams = queryParams;
  }

  public boolean isAddressType() {
    return url.startsWith("/addresses");
  }

  public boolean isCaseType() {
    return url.startsWith("/cases");
  }
}
