package uk.gov.ons.ctp.integration.mock.endpoint;

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
import uk.gov.ons.ctp.integration.mock.data.DataRepository;

/**
 * This class holds the /help endpoint to provide a list of supported endpoints and the available
 * data.
 */
@RestController
@RequestMapping(value = "/help", produces = "application/json")
public final class HelpEndpoint implements CTPEndpoint {
  private static final Pattern UPRN_PATTERN = Pattern.compile("\"uprn\"\\s*:");
  private static final Pattern FIRST_UPRN_PATTERN = Pattern.compile("\"firstUprn\"\\s*:");

  @Value("${server.port}")
  private String port;

  @RequestMapping(value = "", method = RequestMethod.GET)
  public ResponseEntity<String> help() throws IOException {
    StringBuilder helpText = new StringBuilder();
    helpText.append("HELP ENDPOINT DESCRIPTIONS\n\n");
    helpText.append(
        "  /help/addresses              - description and examples for addresses endpoint\n");
    helpText.append(
        "  /help/addresses/data         - summary of data held for addresses endpoint\n");
    helpText.append(
        "  /help/capture/addresses      - description and examples for capturing addresses\n");
    helpText.append(
        "  /help/cases                  - description and examples for cases endpoint\n");
    helpText.append("  /help/cases/data             - summary of data held for cases endpoint\n");

    helpText.append("\n\n");
    helpText.append("EXAMPLE COMMANDS\n");
    helpText.append("\n");
    curlHelp(helpText, "help/addresses");
    curlHelp(helpText, "help/addresses/data");
    curlHelp(helpText, "help/capture/addresses");
    curlHelp(helpText, "help/cases");
    curlHelp(helpText, "help/cases/data");

    return ResponseEntity.ok(helpText.toString());
  }

  @RequestMapping(value = "/addresses", method = RequestMethod.GET)
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
    for (var requestType : RequestType.values()) {
      if (requestType.isAddressType()) {
        helpText.append("\n");
        describeUrl(helpText, requestType, "");
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

    return ResponseEntity.ok(helpText.toString());
  }

  @RequestMapping(value = "/addresses/data", method = RequestMethod.GET)
  public ResponseEntity<String> addressDataHelp() throws Exception {
    StringBuilder helpText = new StringBuilder();
    helpText.append("MOCK AI DATA HELP\n");
    helpText.append("  The mock endpoints currently hold the following data:\n");
    helpText.append("  (Number of results returned are in parenthesis)\n");
    for (var requestType : RequestType.values()) {
      if (requestType.isAddressType()) {
        buildDataHelp(helpText, requestType);
      }
    }
    return ResponseEntity.ok(helpText.toString());
  }

  @RequestMapping(value = "/capture/addresses", method = RequestMethod.GET)
  public ResponseEntity<String> captureHelp() throws IOException {

    StringBuilder helpText = new StringBuilder();

    helpText.append("CAPTURE ENDPOINTS\n");
    helpText.append("  These allow the dataset used by mock-ai to be extended at run time.\n");
    helpText.append("  If an endpoint URL is prefixed with '/capture' then the request is\n");
    helpText.append("  initially sent to AI. The AI response is then written to file, so\n");
    helpText.append("  that a subsequent request to the corresponding endpoint will respond\n");
    helpText.append("  with the newly capture data.\n");
    for (var requestType : RequestType.values()) {
      if (requestType.isAddressType()) {
        helpText.append("\n");
        describeUrl(helpText, requestType, "/capture");
        describeQueryParams(helpText, requestType);
      }
    }
    return ResponseEntity.ok(helpText.toString());
  }

  @RequestMapping(value = "/cases", method = RequestMethod.GET)
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
    for (var requestType : RequestType.values()) {
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

    return ResponseEntity.ok(helpText.toString());
  }

  @RequestMapping(value = "/cases/data", method = RequestMethod.GET)
  public ResponseEntity<String> caseDataHelp() throws Exception {
    StringBuilder helpText = new StringBuilder();

    helpText.append("MOCK CASE DATA HELD\n");
    helpText.append("  The mock endpoints currently hold the following data:\n");
    for (var requestType : RequestType.values()) {
      if (requestType.isCaseType()) {
        buildDataHelp(helpText, requestType);
      }
    }
    return ResponseEntity.ok(helpText.toString());
  }

  private void buildDataHelp(StringBuilder helpText, RequestType requestType) throws Exception {
    helpText.append("\n");
    helpText.append("  " + requestType.getUrl() + "\n");

    List<String> dataFiles = DataRepository.list(requestType);
    Properties props = DataRepository.getInventory(requestType);

    for (String name : dataFiles) {
      String normalisedName = DataRepository.normaliseFileName(name);
      String dataDescription = props.getProperty(normalisedName, null);
      Integer count = null;
      if (requestType.isAddressType()) {
        count = count(normalisedName, requestType);
      }
      String dataText = describeData(normalisedName, count, dataDescription);
      helpText.append("    " + dataText + "\n");
    }
  }

  private StringBuilder curlHelp(StringBuilder helpText, String path) {
    return helpText.append("  $ curl -s localhost:" + port + "/" + path + "\n");
  }

  private void describeUrl(
      StringBuilder helpText,
      uk.gov.ons.ctp.integration.mock.endpoint.RequestType requestType,
      String urlPrefix) {
    helpText.append("  " + urlPrefix + requestType.getUrl() + "\n");
    helpText.append("      " + requestType.getDescription() + "\n");
  }

  private void describeQueryParams(StringBuilder helpText, RequestType requestType) {
    String[] queryParams = requestType.getQueryParams();
    if (queryParams.length > 0) {
      helpText.append("      Query parameters:\n");
      for (String queryParam : queryParams) {
        helpText.append("        - " + queryParam + "\n");
      }
    }
  }

  private String describeData(String normalisedName, Integer count, String dataDescription) {
    if (count != null) {
      normalisedName = String.format("%s (%d)", normalisedName, count);
    }
    if (dataDescription == null) {
      return normalisedName;
    }
    return String.format("%-20s(%s)", normalisedName, dataDescription);
  }

  private int count(String baseFileName, RequestType requestType) throws Exception {
    if (requestType.isCaseType()) {
      throw new IllegalArgumentException("count call not for case types!");
    }
    int resultCount = 0;
    String json = DataRepository.read(requestType, baseFileName);
    for (var p : List.of(UPRN_PATTERN, FIRST_UPRN_PATTERN)) {
      Matcher m = p.matcher(json);
      while (m.find()) {
        resultCount++;
      }
    }
    return resultCount;
  }
}
