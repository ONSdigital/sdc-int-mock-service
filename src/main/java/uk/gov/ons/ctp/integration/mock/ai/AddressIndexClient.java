package uk.gov.ons.ctp.integration.mock.ai;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.integration.mock.Constants;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPartialAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexRhPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.endpoint.RequestType;

/**
 * This class is responsible for talking to Address Index.
 *
 * <p>It's used to capture AI responses that can be used for AI simulation.
 */
@Slf4j
public class AddressIndexClient {
  private RestClient restClient;
  private String aiToken;

  public AddressIndexClient(RestClient restClient, String aiToken) throws CTPException {
    this.restClient = restClient;
    this.aiToken = aiToken.trim();
  }

  /**
   * Get AI address data by postcode. RH version.
   *
   * @throws CTPException
   */
  public Object getAddressesRhPostcode(String postcode) throws CTPException {
    ArrayList<AddressIndexRhPostcodeResultsDTO> results = new ArrayList<>();
    int offset = 0;
    int batchSize = 100;

    while (offset < Constants.CAPTURE_MAXIMUM_RESULTS) {
      AddressIndexRhPostcodeResultsDTO response =
          (AddressIndexRhPostcodeResultsDTO)
              invokeAI(RequestType.AI_RH_POSTCODE, null, offset, batchSize, postcode);
      results.add(response);

      int numFound = response.getResponse().getAddresses().size();
      if (numFound == 0) {
        break;
      }

      offset += numFound;
    }

    // Amalgamate all results into single data set
    ArrayList<AddressIndexRhPostcodeAddressDTO> allResponses = new ArrayList<>();
    for (AddressIndexRhPostcodeResultsDTO r : results) {
      allResponses.addAll(r.getResponse().getAddresses());
    }

    AddressIndexRhPostcodeResultsDTO result = results.get(0);
    result.getResponse().setAddresses(allResponses);
    result.getResponse().setLimit(-1);

    return result;
  }

  public AddressIndexPartialResultsDTO getAddressesPartial(String input) throws CTPException {
    ArrayList<AddressIndexPartialResultsDTO> results = new ArrayList<>();
    int offset = 0;
    int batchSize = 100;

    while (offset < Constants.CAPTURE_MAXIMUM_RESULTS) {
      MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
      queryParams.add("input", input);

      AddressIndexPartialResultsDTO response =
          (AddressIndexPartialResultsDTO)
              invokeAI(RequestType.AI_PARTIAL, queryParams, offset, batchSize, input);
      results.add(response);

      int numFound = response.getResponse().getAddresses().size();
      if (numFound == 0) {
        break;
      }

      offset += numFound;
    }

    // Amalgamate all results into single data set
    ArrayList<AddressIndexPartialAddressDTO> allResponses = new ArrayList<>();
    for (AddressIndexPartialResultsDTO r : results) {
      allResponses.addAll(r.getResponse().getAddresses());
    }

    AddressIndexPartialResultsDTO result = results.get(0);
    result.getResponse().setAddresses(allResponses);
    result.getResponse().setLimit(-1);

    return result;
  }

  public AddressIndexPostcodeResultsDTO getAddressesPostcode(String postcode) throws CTPException {
    ArrayList<AddressIndexPostcodeResultsDTO> results = new ArrayList<>();
    int offset = 0;
    int batchSize = 100;

    while (offset < Constants.CAPTURE_MAXIMUM_RESULTS) {
      AddressIndexPostcodeResultsDTO response =
          (AddressIndexPostcodeResultsDTO)
              invokeAI(RequestType.AI_POSTCODE, null, offset, batchSize, postcode);
      results.add(response);

      int numFound = response.getResponse().getAddresses().size();
      if (numFound == 0) {
        break;
      }

      offset += numFound;
    }

    // Amalgamate all results into single data set
    ArrayList<AddressIndexPostcodeAddressDTO> allResponses = new ArrayList<>();
    for (AddressIndexPostcodeResultsDTO r : results) {
      allResponses.addAll(r.getResponse().getAddresses());
    }

    AddressIndexPostcodeResultsDTO result = results.get(0);
    result.getResponse().setAddresses(allResponses);
    result.getResponse().setLimit(-1);

    return result;
  }

  public Object getAddressesEq(String input) throws CTPException {
    MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
    queryParams.add("input", input);

    // Decide if search is for a postcode, as AI will then have different limit and structure
    boolean isPostcodeBasedSearch = input.matches("[A-Za-z]{1,2}[0-9].*");
    int limit = isPostcodeBasedSearch ? 100 : 20;
    RequestType requestType = isPostcodeBasedSearch ? RequestType.AI_EQ_POSTCODE : RequestType.AI_EQ;

    Object response = invokeAI(requestType, queryParams, 0, limit, (String) null);

    return response;
  }

  public Object getAddressesRhUprn(String uprn) throws CTPException {
    int offset = -1;
    int limit = -1;
    return invokeAI(RequestType.AI_RH_UPRN, null, offset, limit, uprn);
  }

  private Object invokeAI(
      RequestType requestType,
      MultiValueMap<String, String> queryParams,
      int offset,
      int limit,
      String... pathParams)
      throws CTPException {

    // Fail if the AI security token has not been set
    if (this.aiToken.isEmpty()) {
      log.error("Address Index token not set. Unable to contact AI.", kv("TokenName", "AI_TOKEN"));
      throw new CTPException(Fault.RESOURCE_NOT_FOUND, "AI token not set: " + "AI_TOKEN");
    }

    if (queryParams == null) {
      queryParams = new LinkedMultiValueMap<String, String>();
    }
    queryParams.add("offset", Integer.toString(offset));
    queryParams.add("limit", Integer.toString(limit));

    Map<String, String> headerParams = new HashMap<String, String>();
    headerParams.put("Authorization: ", "Bearer " + aiToken);

    Object response =
        restClient.getResource(
            requestType.getUrl(),
            requestType.getResponseClass(),
            headerParams,
            queryParams,
            (Object[]) pathParams);

    return response;
  }
}
