package uk.gov.ons.ctp.integration.mock.endpoint;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;
import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.v;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.SingleUseQuestionnaireIdDTO;
import uk.gov.ons.ctp.integration.mock.FailureSimulator;
import uk.gov.ons.ctp.integration.mock.ai.model.request.AddressesRhUprnRequestDTO;
import uk.gov.ons.ctp.integration.mock.data.DataRepository;

/** Provides mock endpoints for the case service. */
@Slf4j
@RestController
@RequestMapping(value = "/cases", produces = "application/json")
public final class CaseServiceEndpoint implements CTPEndpoint {
  private static final int UAC_LENGTH = 16;

  @RequestMapping(value = "/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok("SDC MOCK CASE SERVICE");
  }

  /**
   * the GET endpoint to find a Case by UUID
   *
   * @param caseId to find by
   * @param includeCaseEvents flag used to return or not CaseEvents
   * @return the case found
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<Object> findCaseById(
      @PathVariable(value = "caseId") String caseId,
      @Valid AddressesRhUprnRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.CASE_ID;
    log.info("Request {}/{}", requestType.getPath(), v("caseId", caseId));

    return DataRepository.simulateResponse(requestType, caseId, 0, 1);
  }

  /**
   * the GET endpoint to generate a new Questionnaire Id for a case.
   *
   * @param caseId to find by
   * @return the new questionnaire id
   */
  @RequestMapping(value = "/{caseId}/qid", method = RequestMethod.GET)
  public ResponseEntity<SingleUseQuestionnaireIdDTO> newQuestionnaireIdForCase(
      @PathVariable("caseId") String caseId,
      @RequestParam(required = false) final boolean individual,
      @RequestParam(required = false) final UUID individualCaseId)
      throws IOException, CTPException {
    log.debug(
        "Entering newQuestionnaireIdForCase",
        kv("case_id", caseId),
        kv("individual", individual),
        kv("individualCaseId", individualCaseId));

    FailureSimulator.optionallyTriggerFailure(caseId, 400, 401, 404, 500);

    String caseResult = DataRepository.read(RequestType.CASE_QID, caseId);
    Object test =
        new ObjectMapper().readerFor(SingleUseQuestionnaireIdDTO.class).readValue(caseResult);
    SingleUseQuestionnaireIdDTO caseDetails = (SingleUseQuestionnaireIdDTO) test;
    nullTestThrowsException(caseDetails);

    if (!individual && individualCaseId != null) {
      throw new IllegalStateException("Can't supply individualCaseId if not for an individual");
    }

    SingleUseQuestionnaireIdDTO newQuestionnaire = new SingleUseQuestionnaireIdDTO();
    newQuestionnaire.setQuestionnaireId(
        String.format("%010d", new Random().nextInt(Integer.MAX_VALUE)));
    newQuestionnaire.setUac(RandomStringUtils.randomAlphanumeric(UAC_LENGTH));
    newQuestionnaire.setFormType(caseDetails.getFormType());
    newQuestionnaire.setQuestionnaireType("1");

    return ResponseEntity.ok(newQuestionnaire);
  }

  /**
   * the GET endpoint to find a Case by UPRM
   *
   * @param uprn to find by
   * @return the case found
   */
  @RequestMapping(value = "/uprn/{uprn}", method = RequestMethod.GET)
  public ResponseEntity<Object> findCaseByUPRN(
      @PathVariable(value = "uprn") String uprn, @Valid AddressesRhUprnRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.CASE_UPRN;
    log.info("Request {}/{}", requestType.getPath(), v("uprn", uprn));

    return DataRepository.simulateResponse(requestType, uprn, 0, 1);
  }

  /**
   * the GET endpoint to find a Case by caseRef
   *
   * @param ref to find by
   * @return the case found
   */
  @RequestMapping(value = "/ref/{ref}", method = RequestMethod.GET)
  public ResponseEntity<Object> findCaseByCaseReference(
      @PathVariable(value = "ref") String ref, @Valid AddressesRhUprnRequestDTO requestParamsDTO)
      throws IOException, CTPException {

    RequestType requestType = RequestType.CASE_REF;
    log.info("Request {}/{}", requestType.getPath(), v("ref", ref));

    return DataRepository.simulateResponse(requestType, ref, 0, 1);
  }

  private void nullTestThrowsException(Object response) {
    if (response == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }
}
