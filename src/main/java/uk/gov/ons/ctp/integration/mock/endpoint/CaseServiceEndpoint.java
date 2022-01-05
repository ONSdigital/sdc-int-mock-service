package uk.gov.ons.ctp.integration.mock.endpoint;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;
import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.v;

import java.io.IOException;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.domain.UniquePropertyReferenceNumber;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mock.FailureSimulator;

/** Provides mock endpoints for the case service. */
@Slf4j
@RestController
@RequestMapping(value = "/cases", produces = "application/json")
public final class CaseServiceEndpoint implements CTPEndpoint {

  /**
   * the GET endpoint to find a Case by UUID
   *
   * @param caseId to find by
   * @param includeCaseEvents flag used to return or not CaseEvents
   * @return the case found
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<?> findCaseById(
      @PathVariable("caseId") final UUID caseId,
      @RequestParam(value = "caseEvents", required = false) boolean includeCaseEvents)
      throws IOException, CTPException {
    RequestType requestType = RequestType.CASE_ID;
    log.info("Request {}/{}", requestType.getPath(), v("caseId", caseId));
    FailureSimulator.optionallyTriggerFailure(caseId.toString(), 400, 401, 404, 500);
    return ResponseBuilder.respond(requestType, caseId.toString(), 0, 1);
  }

  /**
   * the GET endpoint to generate a new Questionnaire Id for a case.
   *
   * @param caseId to find by
   * @return the new questionnaire id
   */
  @RequestMapping(value = "/{caseId}/telephone-capture", method = RequestMethod.GET)
  public ResponseEntity<?> newQuestionnaireIdForCase(@PathVariable("caseId") String caseId)
      throws IOException, CTPException {
    log.info("Entering newQuestionnaireIdForCase {}", kv("case_id", caseId));

    FailureSimulator.optionallyTriggerFailure(caseId, 400, 401, 404, 500);
    RequestType requestType = RequestType.CASE_QID;
    return ResponseBuilder.respond(requestType, caseId.toString(), 0, 1);
  }

  /**
   * the GET endpoint to find a Case by UPRN
   *
   * @param uprn to find by
   * @return the case found
   */
  @RequestMapping(value = "/uprn/{uprn}", method = RequestMethod.GET)
  public ResponseEntity<?> findCaseByUPRN(
      @PathVariable(value = "uprn") final UniquePropertyReferenceNumber uprn)
      throws IOException, CTPException {
    RequestType requestType = RequestType.CASE_UPRN;
    log.info("Request {}/{}", requestType.getPath(), v("uprn", uprn));
    String uprnStr = Long.toString(uprn.getValue());
    FailureSimulator.optionallyTriggerFailure(uprnStr, 400, 401, 404, 500);
    return ResponseBuilder.respond(requestType, uprnStr, 0, 1);
  }

  /**
   * the GET endpoint to find a Case by caseRef
   *
   * @param ref to find by
   * @return the case found
   */
  @RequestMapping(value = "/ref/{ref}", method = RequestMethod.GET)
  public ResponseEntity<?> findCaseByCaseReference(
      @PathVariable(value = "ref") final long ref,
      @RequestParam(value = "caseEvents", required = false) boolean includeCaseEvents)
      throws IOException, CTPException {

    RequestType requestType = RequestType.CASE_REF;
    log.info("Request {}/{}", requestType.getPath(), v("ref", ref));
    String caseRef = Long.toString(ref);
    FailureSimulator.optionallyTriggerFailure(caseRef, 400, 401, 404, 500);
    return ResponseBuilder.respond(requestType, caseRef, 0, 1);
  }
}
