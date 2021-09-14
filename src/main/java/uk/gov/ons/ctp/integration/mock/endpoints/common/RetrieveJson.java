package uk.gov.ons.ctp.integration.mock.endpoints.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mock.data.CaptureCache;
import uk.gov.ons.ctp.integration.mock.endpoints.RequestType;
import uk.gov.ons.ctp.integration.mock.misc.Constants;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexPartialAddressDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexRhPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.model.ai.AddressIndexRhPostcodeResultsDTO;

@Slf4j
@Configuration
public class RetrieveJson {

  @SuppressWarnings("unchecked")
  public ResponseEntity<Object> simulateResponse(
      RequestType requestType, String name, int offset, int limit)
      throws IOException, CTPException {

    String baseFileName = CaptureCache.normaliseFileName(name);

    log.info(baseFileName);
    HttpStatus responseStatus = HttpStatus.OK;
    Object response = null;
    String responseText = CaptureCache.readCapturedAiResponse(requestType, baseFileName);

    if (responseText != null) {
      // Convert captured AI response to an object, and return the target subset of data
      if (requestType.getResponseClass().equals(String.class)) {
        // Don't push it through Jackson, as it is already in String format
        response = responseText;
      } else {
        response =
            new ObjectMapper().readerFor(requestType.getResponseClass()).readValue(responseText);
      }

      switch (requestType) {
        case AI_RH_POSTCODE:
          AddressIndexRhPostcodeResultsDTO rhPostcodes =
              (AddressIndexRhPostcodeResultsDTO) response;
          List<AddressIndexRhPostcodeAddressDTO> rhPostcodeAddresses =
              (List<AddressIndexRhPostcodeAddressDTO>)
                  subset(rhPostcodes.getResponse().getAddresses(), offset, limit);
          rhPostcodes.getResponse().setAddresses(rhPostcodeAddresses);
          rhPostcodes.getResponse().setOffset(offset);
          rhPostcodes.getResponse().setLimit(limit);
          // Replicate the counting down of the confidence score
          int confidence = 100000 + rhPostcodeAddresses.size();
          for (AddressIndexRhPostcodeAddressDTO address : rhPostcodeAddresses) {
            address.setConfidenceScore(confidence--);
          }
          break;
        case AI_PARTIAL:
          AddressIndexPartialResultsDTO partial = (AddressIndexPartialResultsDTO) response;
          List<AddressIndexPartialAddressDTO> partialAddresses =
              (List<AddressIndexPartialAddressDTO>)
                  subset(partial.getResponse().getAddresses(), offset, limit);
          partial.getResponse().setAddresses(partialAddresses);
          partial.getResponse().setOffset(offset);
          partial.getResponse().setLimit(limit);
          break;
        case AI_POSTCODE:
          AddressIndexPostcodeResultsDTO postcodes = (AddressIndexPostcodeResultsDTO) response;
          List<AddressIndexPostcodeAddressDTO> postcodeAddresses =
              (List<AddressIndexPostcodeAddressDTO>)
                  subset(postcodes.getResponse().getAddresses(), offset, limit);
          postcodes.getResponse().setAddresses(postcodeAddresses);
          postcodes.getResponse().setOffset(offset);
          postcodes.getResponse().setLimit(limit);
        case AI_EQ:
          // Nothing to do for type-ahead response
          break;
        case AI_RH_UPRN:
          // Nothing to do for uprn results
          break;
        case CASE_UPRN:
          // Nothing to do for uprn results
          break;
        case CASE_ID:
          // Nothing to do for uprn results
          break;
        case CASE_QID:
          // Nothing to do for uprn results
          break;
        case CASE_REF:
          // Nothing to do for uprn results
          break;
        default:
          throw new CTPException(
              CTPException.Fault.SYSTEM_ERROR, "Unrecognised request type: " + requestType.name());
      }
    } else {
      // 404 - not found
      responseStatus = requestType.getNotFoundHttpStatus();
      responseText = CaptureCache.readCapturedAiResponse(requestType, Constants.NO_DATA_FILE_NAME);

      // Customise the not-found response by replacing any place holders with actual values
      String placeholderName = requestType.getPlaceholderName();
      if (placeholderName != null) {
        String fullPlaceholderName = "%" + placeholderName + "%";
        responseText = responseText.replace(fullPlaceholderName, name);
      }

      response = responseText;
    }

    return new ResponseEntity<Object>(response, responseStatus);
  }

  private List<?> subset(List<?> addresses, int offset, int limit) {
    if (offset > addresses.size() || offset < 0) {
      return new ArrayList<Object>();
    }

    int toIndex = Math.min(offset + limit, addresses.size());

    return addresses.subList(offset, toIndex);
  }

  public String getCases(RequestType requestType, String name) throws IOException, CTPException {
    String baseFileName = CaptureCache.normaliseFileName(name);

    return CaptureCache.readCapturedAiResponse(requestType, baseFileName);
  }
}
