package uk.gov.ons.ctp.integration.mock.endpoint;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.v;

import java.io.IOException;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mock.ai.model.request.AddressesPartialRequestDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.request.AddressesPostcodeRequestDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.request.AddressesRhPostcodeRequestDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.request.AddressesRhUprnRequestDTO;

/** Provides mock endpoints for a subset of the AI /addresses endpoints. */
@Slf4j
@RestController
@RequestMapping(value = "/addresses", produces = "application/json")
public final class AddressesEndpoint implements CTPEndpoint {

  @RequestMapping(value = "/rh/postcode/{postcode}", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesRhPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_RH_POSTCODE;
    postcode = postcode.replaceAll("\\s", "");
    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

    return ResponseBuilder.respond(
        requestType, postcode, requestParamsDTO.getOffset(), requestParamsDTO.getLimit());
  }

  @RequestMapping(value = "/partial", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesPartial(
      @RequestParam(required = true) String input,
      @Valid AddressesPartialRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_PARTIAL;
    log.info("Request {}", requestType.getPath() + "?input=" + input);

    return ResponseBuilder.respond(
        requestType, input, requestParamsDTO.getOffset(), requestParamsDTO.getLimit());
  }

  @RequestMapping(value = "/postcode/{postcode}", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesPostcodeRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_POSTCODE;
    postcode = postcode.replaceAll("\\s", "");
    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

    return ResponseBuilder.respond(
        requestType, postcode, requestParamsDTO.getOffset(), requestParamsDTO.getLimit());
  }

  @RequestMapping(value = "/rh/uprn/{uprn}", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesRhUprn(
      @PathVariable(value = "uprn") String uprn, @Valid AddressesRhUprnRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_RH_UPRN;
    log.info("Request {}/{}", requestType.getPath(), v("uprn", uprn));

    return ResponseBuilder.respond(requestType, uprn, 0, 1);
  }

  @RequestMapping(value = "/eq", method = RequestMethod.GET)
  public ResponseEntity<Object> getAddressesEq(@RequestParam(required = true) String input)
      throws IOException, CTPException {

    // Match AI behaviour, returns a different response structure if it detects a postcode
    boolean isPostcodeBasedSearch = input.matches("[A-Za-z]{1,2}[0-9].*");
    RequestType requestType =
        isPostcodeBasedSearch ? RequestType.AI_EQ_POSTCODE : RequestType.AI_EQ;

    log.info("Request {}", requestType.getUrl() + "?input=" + input);

    return ResponseBuilder.respond(requestType, input, 0, 10);
  }
}
