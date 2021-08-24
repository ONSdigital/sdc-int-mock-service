package uk.gov.ons.ctp.integration.mock.endpoints;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import uk.gov.ons.ctp.common.domain.CaseType;
import uk.gov.ons.ctp.common.domain.FormType;
import uk.gov.ons.ctp.common.domain.UniquePropertyReferenceNumber;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.CaseContainerDTO;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.EventDTO;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.QuestionnaireIdDTO;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.SingleUseQuestionnaireIdDTO;
import uk.gov.ons.ctp.integration.mock.config.CasesConfig;
import uk.gov.ons.ctp.integration.mock.config.QuestionnairesConfig;
import uk.gov.ons.ctp.integration.mock.misc.FailureSimulator;
import uk.gov.ons.ctp.integration.mock.model.caserequest.CaseQueryRequestDTO;
import uk.gov.ons.ctp.integration.mock.model.caserequest.ResponseDTO;
import uk.gov.ons.ctp.integration.mock.validator.CaseServiceRequestDTO;

/** Provides mock endpoints for the case service. */
@Slf4j
@RestController
@RequestMapping(value = "/cases", produces = "application/json")
public final class CaseServiceEndpoint implements CTPEndpoint {
  private static final int UAC_LENGTH = 16;

  private static volatile long FIND_CASE_BY_UPRN_SLEEP_TIME = 10;

  private static AtomicInteger concurrentCounterFindCaseByUPRN = new AtomicInteger(0);

  @Autowired private CasesConfig casesConfig; // can allow field injection here in a mock service.
  @Autowired private QuestionnairesConfig questionnairesConfig;

  @RequestMapping(value = "/info", method = RequestMethod.GET)
  public ResponseEntity<String> info() {
    return ResponseEntity.ok("SDC MOCK CASE SERVICE");
  }

  @RequestMapping(value = "/examples", method = RequestMethod.GET)
  public ResponseEntity<String> examples() {
    return ResponseEntity.ok(
        "CASES-- "
            + casesConfig.getCases()
            + " -- QUESTIONNAIRES-- "
            + questionnairesConfig.getQuestionnaires());
  }

  /**
   * the GET endpoint to find a Case by UUID
   *
   * @param caseId to find by
   * @param includeCaseEvents flag used to return or not CaseEvents
   * @return the case found
   */
  @RequestMapping(value = "/{caseId}", method = RequestMethod.GET)
  public ResponseEntity<CaseContainerDTO> findCaseById(
      @PathVariable("caseId") final UUID caseId,
      @RequestParam(value = "caseEvents", required = false) boolean includeCaseEvents) {
    log.debug("Entering findCaseById", kv("case_id", caseId));

    FailureSimulator.optionallyTriggerFailure(caseId.toString(), 400, 401, 404, 500);
    CaseContainerDTO caseDetails = casesConfig.getCaseByUUID(caseId.toString());
    nullTestThrowsException(caseDetails);
    caseDetails.setCaseEvents(getCaseEvents(caseDetails.getId().toString(), includeCaseEvents));
    return ResponseEntity.ok(caseDetails);
  }

  /**
   * the GET endpoint to generate a new Questionnaire Id for a case.
   *
   * @param caseId to find by
   * @return the new questionnaire id
   */
  @RequestMapping(value = "/{caseId}/qid", method = RequestMethod.GET)
  public ResponseEntity<SingleUseQuestionnaireIdDTO> newQuestionnaireIdForCase(
      @PathVariable("caseId") final UUID caseId,
      @RequestParam(required = false) final boolean individual,
      @RequestParam(required = false) final UUID individualCaseId) {
    log.debug(
        "Entering newQuestionnaireIdForCase",
        kv("case_id", caseId),
        kv("individual", individual),
        kv("individualCaseId", individualCaseId));

    FailureSimulator.optionallyTriggerFailure(caseId.toString(), 400, 401, 404, 500);

    CaseContainerDTO caseDetails = casesConfig.getCaseByUUID(caseId.toString());
    nullTestThrowsException(caseDetails);

    if (individual == false && individualCaseId != null) {
      throw new IllegalStateException("Can't supply individualCaseId if not for an individual");
    }

    CaseServiceRequestDTO.validateGetNewQidByCaseIdRequest(
        caseDetails, individual, individualCaseId);

    SingleUseQuestionnaireIdDTO newQuestionnaire = new SingleUseQuestionnaireIdDTO();
    newQuestionnaire.setQuestionnaireId(
        String.format("%010d", new Random().nextInt(Integer.MAX_VALUE)));
    newQuestionnaire.setUac(RandomStringUtils.randomAlphanumeric(UAC_LENGTH));
    newQuestionnaire.setFormType(formType(caseDetails.getCaseType()).name());
    newQuestionnaire.setQuestionnaireType("1");

    return ResponseEntity.ok(newQuestionnaire);
  }

  private FormType formType(String caseType) {
    return CaseType.CE.name().equals(caseType) ? FormType.C : FormType.H;
  }

  /**
   * the GET endpoint to find a Case by UPRM
   *
   * @param uprn to find by
   * @return the case found
   */
  @RequestMapping(value = "/uprn/{uprn}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseContainerDTO>> findCaseByUPRN(
      @PathVariable(value = "uprn") final UniquePropertyReferenceNumber uprn) {
    int numConcurrent = concurrentCounterFindCaseByUPRN.incrementAndGet();
    log.debug("Entering findCaseByUPRN: ConcurrentCount: " + numConcurrent, kv("uprn", uprn));

    try {
      Thread.sleep(FIND_CASE_BY_UPRN_SLEEP_TIME);
    } catch (InterruptedException e) {
      log.error("Sleep interrupted", e);
    }

    FailureSimulator.optionallyTriggerFailure(Long.toString(uprn.getValue()), 400, 401, 404, 500);
    List<CaseContainerDTO> cases = casesConfig.getCaseByUprn(Long.toString(uprn.getValue()));
    nullTestThrowsException(cases);

    numConcurrent = concurrentCounterFindCaseByUPRN.decrementAndGet();
    log.debug("Concurrent count on exit: " + numConcurrent);

    return ResponseEntity.ok(cases);
  }

  /**
   * the GET endpoint to find a Case by caseRef
   *
   * @param ref to find by
   * @return the case found
   */
  @RequestMapping(value = "/ref/{ref}", method = RequestMethod.GET)
  public ResponseEntity<CaseContainerDTO> findCaseByCaseReference(
      @PathVariable(value = "ref") final long ref, @Valid CaseQueryRequestDTO requestParamsDTO) {
    log.info(
        "Entering GET getCaseByCaseReference",
        kv("ref", ref),
        kv("caseEvents", requestParamsDTO.getCaseEvents()));

    FailureSimulator.optionallyTriggerFailure(Long.toString(ref), 400, 401, 404, 500);

    CaseContainerDTO caseDetails = casesConfig.getCaseByRef(Long.toString(ref));
    nullTestThrowsException(caseDetails);
    caseDetails.setCaseEvents(
        getCaseEvents(caseDetails.getId().toString(), requestParamsDTO.getCaseEvents()));
    return ResponseEntity.ok(caseDetails);
  }

  /**
   * Post a list of Cases in order to add cases to, or replace cases in, the case maps driving the
   * responses here.
   *
   * @param requestBody - a list of cases
   * @return - response confirming post.
   */
  @RequestMapping(value = "/data/cases/save", method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<ResponseDTO> addOrReplaceCaseData(
      @RequestBody List<CaseContainerDTO> requestBody) throws CTPException {

    log.info("Entering POST addOrReplaceCaseData", kv("requestBody", requestBody));
    casesConfig.addOrReplaceData(requestBody);

    return ResponseEntity.ok(createResponseDTO("MockCaseSaveService"));
  }

  /**
   * reset the application case data back to the original JSON
   *
   * @return - response confirming success.
   */
  @RequestMapping(value = "/data/cases/reset", method = RequestMethod.GET)
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<ResponseDTO> resetCaseData() throws CTPException {
    try {
      casesConfig.resetData();
      return ResponseEntity.ok(createResponseDTO("MockCaseResetService"));
    } catch (IOException e) {
      throw new CTPException(
          CTPException.Fault.BAD_REQUEST, "Unable to reset Case Data - IO Exception  reading JSON");
    }
  }

  /**
   * Post a list of Questionnaires in order to add to the questionnaire maps driving the responses
   * here.
   *
   * @param requestBody - a list of Questionnaires
   * @return - response confirming post.
   */
  @RequestMapping(value = "/data/questionnaires/add", method = RequestMethod.POST)
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<ResponseDTO> addQuestionnaireData(
      @Valid @RequestBody List<QuestionnaireIdDTO> requestBody) throws CTPException {

    log.info("Entering POST addQData", kv("requestBody", requestBody));
    questionnairesConfig.addData(requestBody);
    return ResponseEntity.ok(createResponseDTO("MockQuestionnaireAddService"));
  }

  /**
   * reset the application data back to the original JSON
   *
   * @return - response confirming post.
   */
  @RequestMapping(value = "/data/questionnaires/reset", method = RequestMethod.GET)
  @ResponseStatus(value = HttpStatus.OK)
  public ResponseEntity<ResponseDTO> resetQuestionnaireData() throws CTPException {
    try {
      questionnairesConfig.resetData();
      return ResponseEntity.ok(createResponseDTO("MockQuestionnaireResetService"));
    } catch (IOException e) {
      throw new CTPException(
          CTPException.Fault.BAD_REQUEST,
          "Unable to reset Questionnaire Data - IO Exception  reading JSON");
    }
  }

  private void nullTestThrowsException(Object response) {
    if (response == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }
  }

  private List<EventDTO> getCaseEvents(final String caseID, final boolean includeCaseEvents) {
    final List<EventDTO> caseEvents = new ArrayList<>();
    if (!includeCaseEvents) {
      return caseEvents;
    }
    return casesConfig.getEventsByCaseID(caseID);
  }

  private ResponseDTO createResponseDTO(final String id) {
    final ResponseDTO responseDTO = new ResponseDTO();
    responseDTO.setId(id);
    responseDTO.setDateTime(new Date());
    return responseDTO;
  }
}
