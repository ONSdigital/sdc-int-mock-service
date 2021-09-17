package uk.gov.ons.ctp.integration.mock.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mock.Constants;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPartialAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexRhPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.data.DataRepository;

/** Build response from JSON data and respond as though the original service had responded. */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseBuilder {

  @SuppressWarnings("unchecked")
  public static ResponseEntity<Object> respond(
      RequestType requestType, String name, int offset, int limit)
      throws IOException, CTPException {

    HttpStatus responseStatus = HttpStatus.OK;
    Object response = null;
    String responseText = DataRepository.read(requestType, name);

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
        case AI_RH_UPRN:
        case CASE_UPRN:
        case CASE_ID:
        case CASE_QID:
        case CASE_REF:
          // Nothing to do these types
          break;
        default:
          throw new CTPException(
              CTPException.Fault.SYSTEM_ERROR, "Unrecognised request type: " + requestType.name());
      }
    } else {
      // 404 - not found
      response = notFound(requestType, name);
    }
    return new ResponseEntity<Object>(response, responseStatus);
  }

  private static String notFound(RequestType requestType, String name)
      throws IOException, CTPException {
    String responseText;
    if (requestType.isAddressType()) {
      responseText = DataRepository.read(requestType, Constants.NO_DATA_FILE_NAME);
    } else {
      responseText = null;
    }
    if (responseText == null) {
      responseText = "Data not found";
    } else {
      // Customise the not-found response by replacing any place holders with actual values
      String placeholderName = requestType.getPlaceholderName();
      String fullPlaceholderName = "%" + placeholderName + "%";
      responseText = responseText.replace(fullPlaceholderName, name);
    }
    return responseText;
  }

  private static List<?> subset(List<?> addresses, int offset, int limit) {
    if (offset > addresses.size() || offset < 0) {
      return new ArrayList<Object>();
    }
    int toIndex = Math.min(offset + limit, addresses.size());
    return addresses.subList(offset, toIndex);
  }
}
