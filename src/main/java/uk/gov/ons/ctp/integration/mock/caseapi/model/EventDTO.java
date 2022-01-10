package uk.gov.ons.ctp.integration.mock.caseapi.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class EventDTO {
  private UUID id;

  private EventType eventType;

  private String description;

  private OffsetDateTime createdDateTime;
}
