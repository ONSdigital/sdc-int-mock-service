package uk.gov.ons.ctp.integration.mock.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;
import uk.gov.ons.ctp.integration.mock.ai.AddressIndexConfig;

/** Application Config bean */
@Validated
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private AddressIndexConfig addressIndex;
}
