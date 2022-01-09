package uk.gov.ons.ctp.integration.mock.caseapi.model;

public enum EventType {
  NEW_CASE,
  RECEIPT,
  REFUSAL,
  INVALID_CASE,
  EQ_LAUNCH,
  UAC_AUTHENTICATION,
  PRINT_FULFILMENT,
  EXPORT_FILE,
  TELEPHONE_CAPTURE,
  DEACTIVATE_UAC,
  UPDATE_SAMPLE,
  UPDATE_SAMPLE_SENSITIVE,
  SMS_FULFILMENT,
  ACTION_RULE_SMS_REQUEST,
  EMAIL_FULFILMENT,
  ACTION_RULE_EMAIL_REQUEST
}