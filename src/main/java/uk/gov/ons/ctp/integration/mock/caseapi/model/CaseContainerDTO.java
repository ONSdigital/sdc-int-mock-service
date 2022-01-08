package uk.gov.ons.ctp.integration.mock.caseapi.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

@Data
public class CaseContainerDTO {
  private String caseRef;

  private OffsetDateTime createdAt;

  private OffsetDateTime lastUpdatedAt;

  private List<EventDTO> caseEvents;

  private UUID id;

  boolean invalid;

  RefusalType refusalReceived;

  Map<String, String> sample;
}
