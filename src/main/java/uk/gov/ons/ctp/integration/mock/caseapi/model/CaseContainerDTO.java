package uk.gov.ons.ctp.integration.mock.caseapi.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.Data;

/**
 * By having these model classes, the mock stands apart from the case api client in common.
 * It's understanding of what it is mocking is separate from the common clients understanding - in
 * particular, it uses exactly the same datetime types that RM uses.
 * @author philwhiles
 *
 */
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
