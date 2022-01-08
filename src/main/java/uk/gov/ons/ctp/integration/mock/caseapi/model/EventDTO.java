package uk.gov.ons.ctp.integration.mock.caseapi.model;

import java.time.OffsetDateTime;
import java.util.UUID;
import lombok.Data;

@Data
public class EventDTO {
  private UUID id;

  private EventType eventType;

  private String description;

  //  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private OffsetDateTime createdDateTime;
}
