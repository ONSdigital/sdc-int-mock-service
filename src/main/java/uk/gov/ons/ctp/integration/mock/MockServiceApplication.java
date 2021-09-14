package uk.gov.ons.ctp.integration.mock;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.common.rest.RestClientConfig;
import uk.gov.ons.ctp.integration.mock.client.MockServiceClient;
import uk.gov.ons.ctp.integration.mock.config.AppConfig;

/** The 'main' entry point for the Mock Service SpringBoot Application. */
@Slf4j
@SpringBootApplication
@EnableCaching
public class MockServiceApplication {
  @Autowired private AppConfig appConfig;

  public static void main(final String[] args) {
    SpringApplication.run(MockServiceApplication.class, args);
  }

  @Bean
  public RestExceptionHandler restExceptionHandler() {
    return new RestExceptionHandler();
  }

  @Bean
  public MockServiceClient addressIndexClient() throws CTPException {
    log.info("Address Index configuration: {}", appConfig.getAddressIndex());
    RestClientConfig clientConfig = appConfig.getAddressIndex().getRestClientConfig();
    var statusMapping = clientErrorMapping();
    RestClient restClient =
        new RestClient(clientConfig, statusMapping, HttpStatus.INTERNAL_SERVER_ERROR);

    String aiToken = appConfig.getAddressIndex().getToken();
    return new MockServiceClient(restClient, aiToken);
  }

  private Map<HttpStatus, HttpStatus> clientErrorMapping() {
    Map<HttpStatus, HttpStatus> mapping = new HashMap<>();
    EnumSet.allOf(HttpStatus.class).stream()
        .filter(s -> s.is4xxClientError())
        .forEach(s -> mapping.put(s, s));
    return mapping;
  }
}
