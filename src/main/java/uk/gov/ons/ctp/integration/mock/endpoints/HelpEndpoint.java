package uk.gov.ons.ctp.integration.mock.endpoints;

import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mock.data.CaptureCache;

/**
 * This class holds the /help endpoint to provide a list of supported endpoints and the available
 * data.
 */
@RestController
@RequestMapping(value = "", produces = "application/json")
public final class HelpEndpoint implements CTPEndpoint {

  @Value("${server.port}")
  private String port;

  @RequestMapping(value = "addresses/help", method = RequestMethod.GET)
  public ResponseEntity<String> addressHelp() throws IOException {

    StringBuilder helpText = new StringBuilder();

    helpText.append("MOCK AI\n");
    helpText.append("  This mock records and replays data from the Address Index service (AI).\n");

    helpText.append("\n\n");
    helpText.append("MOCK ENDPOINTS\n");
    helpText.append("  The following endpoints mock a subset of the AI endpoints.\n");
    helpText.append(
        "  If mock-service holds data for a request then the response replies with a\n");
    helpText.append("  previously captured AI response. The mock response should be identical \n");
    helpText.append("  to the genuine AI.\n");
    helpText.append("  Endpoints which support offset and limit query parameters return a\n");
    helpText.append("  subset of the data, although it should be noted that the mock holds only\n");
    helpText.append("  the first 1000 or so results from AI.\n");
    for (uk.gov.ons.ctp.integration.mock.endpoints.RequestType requestType :
        uk.gov.ons.ctp.integration.mock.endpoints.RequestType.values()) {
      if (requestType.isAddressType()) {
        helpText.append("\n");
        describeUrl(helpText, requestType, "");
        describeQueryParams(helpText, requestType);
      }
    }

    helpText.append("\n\n");
    helpText.append("MOCK AI Results\n");
    helpText.append(
        "  The following help endpoints will show the total amount of available test data for a given endpoint.\n");
    curlHelp(helpText, "addresses/eq/help");
    curlHelp(helpText, "addresses/postcode/help");
    curlHelp(helpText, "addresses/partial/help");
    curlHelp(helpText, "rh/postcode/help");
    curlHelp(helpText, "rh/uprn/help");

    helpText.append("\n\n");
    helpText.append("CAPTURE ENDPOINTS\n");
    helpText.append("  These allow the dataset used by mock-ai to be extended at run time.\n");
    helpText.append("  If an endpoint URL is prefixed with '/capture' then the request is\n");
    helpText.append("  initially sent to AI. The AI response is then written to file, so\n");
    helpText.append("  that a subsequent request to the corresponding endpoint will respond\n");
    helpText.append("  with the newly capture data.\n");
    for (uk.gov.ons.ctp.integration.mock.endpoints.RequestType requestType :
        uk.gov.ons.ctp.integration.mock.endpoints.RequestType.values()) {
      if (requestType.isAddressType()) {
        helpText.append("\n");
        describeUrl(helpText, requestType, "/capture");
        describeQueryParams(helpText, requestType);
      }
    }

    helpText.append("\n\n");
    helpText.append("EXAMPLE COMMANDS\n");
    helpText.append("\n");

    helpText.append("  Here are some example invocations of the mock-ai");
    curlHelp(helpText, "addresses/partial?input=Treganna");
    curlHelp(helpText, "addresses/partial?input=Treganna&offset=625&limit=85");
    curlHelp(helpText, "addresses/rh/uprn/10013745617");
    curlHelp(helpText, "addresses/rh/postcode/CF32TW");
    curlHelp(helpText, "addresses/postcode/EX24LU");
    curlHelp(helpText, "addresses/eq?input=Holbeche");

    helpText.append("\n\n");
    helpText.append("DATA HELD\n");
    helpText.append("  The mock endpoints currently hold the following data:\n");
    for (uk.gov.ons.ctp.integration.mock.endpoints.RequestType requestType :
        uk.gov.ons.ctp.integration.mock.endpoints.RequestType.values()) {
      if (requestType.isAddressType()) {
        helpText.append("\n");
        helpText.append("  " + requestType.getUrl() + "\n");

        // Get list of data for this request
        List<String> dataFiles = CaptureCache.listCapturedData(requestType);

        // Load optional property file, for descriptions on the data held
        Properties props = CaptureCache.getInventory(requestType);

        // List data items held
        for (String name : dataFiles) {
          String normalisedName = CaptureCache.normaliseFileName(name);
          String dataDescription = props.getProperty(normalisedName, null);
          String dataText = describeData(normalisedName, dataDescription);
          helpText.append("    " + dataText + "\n");
        }
      }
    }

    helpText.append("\n\n");
    helpText.append("MOCK AI Count Endpoints\n");
    helpText.append(
        "  The following help endpoints will show the total amount of available test data for a given endpoint:\n");
    helpText.append("\n\n");
    curlHelp(helpText, "addresses/eq/help");
    curlHelp(helpText, "addresses/partial/help");
    curlHelp(helpText, "addresses/postcode/help");
    curlHelp(helpText, "addresses/rh/postcode/help");
    curlHelp(helpText, "addresses/rh/uprn/help");

    return ResponseEntity.ok(helpText.toString());
  }

  @RequestMapping(value = "cases/help", method = RequestMethod.GET)
  public ResponseEntity<String> caseHelp() throws IOException {
    StringBuilder helpText = new StringBuilder();

    helpText.append("MOCK CONTACT CENTRE\n");
    helpText.append("  This mock retrieves examples Contact Centre (CC) data.\n");

    helpText.append("\n\n");
    helpText.append("MOCK ENDPOINTS\n");
    helpText.append(
        "  The following endpoints mock a subset of the Case and Questionnaire endpoints.\n");
    helpText.append(
        "  If mock-service holds data for a request then the response replies with a\n");
    helpText.append("  previously captured case or questionnaire response.\n");
    for (uk.gov.ons.ctp.integration.mock.endpoints.RequestType requestType :
        uk.gov.ons.ctp.integration.mock.endpoints.RequestType.values()) {
      if (requestType.isCaseType()) {
        helpText.append("\n");
        describeUrl(helpText, requestType, "");
        describeQueryParams(helpText, requestType);
      }
    }

    helpText.append("\n\n");
    helpText.append("EXAMPLE COMMANDS\n");
    helpText.append("\n");

    helpText.append("  Here are some example invocations of the mock CC\n");
    curlHelp(helpText, "cases/examples");
    curlHelp(helpText, "cases/77346443-64ae-422e-9b93-d5250f48a27a");
    curlHelp(helpText, "cases/77346443-64ae-422e-9b93-d5250f48a27a/qid");
    curlHelp(helpText, "cases/uprn/10013047193");
    curlHelp(helpText, "cases/ref/124124009");
    curlHelp(helpText, "addresses/eq?input=Holbeche");

    helpText.append("\n\n");
    helpText.append("MOCK Case Results\n");
    for (uk.gov.ons.ctp.integration.mock.endpoints.RequestType requestType :
        uk.gov.ons.ctp.integration.mock.endpoints.RequestType.values()) {
      if (requestType.isCaseType()) {
        helpText.append("\n");
        helpText.append("  " + requestType.getUrl() + "\n");

        // Get list of data for this request
        List<String> dataFiles = CaptureCache.listCapturedData(requestType);

        // Load optional property file, for descriptions on the data held
        Properties props = CaptureCache.getInventory(requestType);

        // List data items held
        for (String name : dataFiles) {
          String normalisedName = CaptureCache.normaliseFileName(name);
          String dataDescription = props.getProperty(normalisedName, null);
          String dataText = describeData(normalisedName, dataDescription);
          helpText.append("    " + dataText + "\n");
        }
      }
    }
    helpText.append("\n\n");
    helpText.append("MOCK Case Count Endpoints\n");
    helpText.append(
        "  The following help endpoints will show the total amount of available test data for a given endpoint:\n");
    helpText.append("\n\n");
    curlHelp(helpText, "cases/caseid/help");
    curlHelp(helpText, "cases/caseref/help");
    curlHelp(helpText, "cases/uprn/help");
    curlHelp(helpText, "cases/questionnaires/help");

    return ResponseEntity.ok(helpText.toString());
  }

  private StringBuilder curlHelp(StringBuilder helpText, String path) {
    return helpText.append("  $ curl -s localhost:" + port + "/" + path + "\n");
  }

  @RequestMapping(value = "addresses/eq/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpAddressesEq() throws IOException, CTPException {
    RequestType request = RequestType.AI_EQ;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "uprn");
    return ResponseEntity.ok("There are " + resultCount + " eq examples present");
  }

  @RequestMapping(value = "addresses/partial/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpAddressesPartial() throws IOException, CTPException {
    RequestType request = RequestType.AI_PARTIAL;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "uprn");
    return ResponseEntity.ok("There are " + resultCount + " partial examples present");
  }

  @RequestMapping(value = "addresses/postcode/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpAddressesPostcode() throws IOException, CTPException {
    RequestType request = RequestType.AI_POSTCODE;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "uprn");
    return ResponseEntity.ok("There are " + resultCount + " postcode examples present");
  }

  @RequestMapping(value = "addresses/rh/postcode/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpAddressesRhPostcode() throws IOException, CTPException {
    RequestType request = RequestType.AI_RH_POSTCODE;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "uprn");
    return ResponseEntity.ok("There are " + resultCount + " RH postcode examples present");
  }

  @RequestMapping(value = "addresses/rh/uprn/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpAddressesRhUprn() throws IOException, CTPException {
    RequestType request = RequestType.AI_RH_UPRN;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "uprn");
    return ResponseEntity.ok("There are " + resultCount + " RH UPRN examples present");
  }

  @RequestMapping(value = "cases/caseid/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpCases() throws IOException, CTPException {
    RequestType request = RequestType.CASE_ID;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "");
    return ResponseEntity.ok("There are " + resultCount + " CaseId examples present");
  }

  @RequestMapping(value = "cases/uprn/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpCaseUprn() throws IOException, CTPException {
    RequestType request = RequestType.CASE_UPRN;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "");
    return ResponseEntity.ok("There are " + resultCount + " UPRN examples present");
  }

  @RequestMapping(value = "cases/caseref/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpCaseRef() throws IOException, CTPException {
    RequestType request = RequestType.CASE_REF;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "");
    return ResponseEntity.ok("There are " + resultCount + " CaseRef examples present");
  }

  @RequestMapping(value = "cases/questionnaires/help", method = RequestMethod.GET)
  public ResponseEntity<String> helpQuestionnaires() throws IOException, CTPException {
    RequestType request = RequestType.CASE_QID;
    List<String> dataFiles = CaptureCache.listCapturedData(request);
    int resultCount = getCount(dataFiles, request, "");
    return ResponseEntity.ok("There are " + resultCount + " Questionnaire examples present");
  }

  private void describeUrl(
      StringBuilder helpText,
      uk.gov.ons.ctp.integration.mock.endpoints.RequestType requestType,
      String urlPrefix) {
    helpText.append("  " + urlPrefix + requestType.getUrl() + "\n");
    helpText.append("      " + requestType.getDescription() + "\n");
  }

  private void describeQueryParams(
      StringBuilder helpText, uk.gov.ons.ctp.integration.mock.endpoints.RequestType requestType) {
    String[] queryParams = requestType.getQueryParams();
    if (queryParams.length > 0) {
      helpText.append("      Query parameters:\n");
      for (String queryParam : queryParams) {
        helpText.append("        - " + queryParam + "\n");
      }
    }
  }

  private String describeData(String normalisedName, String dataDescription) {
    if (dataDescription == null) {
      return normalisedName;
    }

    return String.format("%-16s(%s)", normalisedName, dataDescription);
  }

  private int getCount(List<String> dataFiles, RequestType requestType, String search)
      throws IOException, CTPException {
    int resultCount = 0;

    if (requestType.isCaseType()) {
      resultCount = dataFiles.size();
    } else {
      String individualJsonResults = "";
      Pattern p = Pattern.compile("\\b" + search + "\\b");
      Matcher m;
      for (String name : dataFiles) {
        String baseFileName = CaptureCache.normaliseFileName(name);
        individualJsonResults = CaptureCache.readCapturedAiResponse(requestType, baseFileName);
        m = p.matcher(individualJsonResults);
        while (m.find()) {
          resultCount++;
        }
      }
    }
    return resultCount;
  }
}
