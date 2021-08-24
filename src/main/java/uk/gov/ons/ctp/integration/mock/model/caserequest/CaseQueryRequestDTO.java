package uk.gov.ons.ctp.integration.mock.model.caserequest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.integration.caseapiclient.caseservice.model.CaseContainerDTO;

/**
 * The request object for a CaseDTOs case event data
 *
 * @author philwhiles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaseQueryRequestDTO {

  private Boolean caseEvents = false;

  private CaseContainerDTO response;
}
