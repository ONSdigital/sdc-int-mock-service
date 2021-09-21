package uk.gov.ons.ctp.integration.mock.endpoint;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;
import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.v;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.CTPException.Fault;
import uk.gov.ons.ctp.integration.mock.ai.AddressIndexClient;
import uk.gov.ons.ctp.integration.mock.ai.model.request.AddressesRhPostcodeRequestDTO;
import uk.gov.ons.ctp.integration.mock.data.DataRepository;

/**
 * This set of endpoints capture AI responses for a subset of the /addresses endpoints.
 *
 * <p>The set of supported endpoints matches those in AddressesEndpoints, except that these are all
 * prefixed with '/capture'.
 */
@Slf4j
@RestController
@RequestMapping(value = "/capture/addresses", produces = "application/json")
public final class CaptureAddressesEndpoint implements CTPEndpoint {
  @Autowired private AddressIndexClient mockServiceClient;

  @RequestMapping(value = "/rh/postcode/{postcode}", method = RequestMethod.GET)
  public Object getAddressesRhPostcode(
      @PathVariable(value = "postcode") String postcode,
      @Valid AddressesRhPostcodeRequestDTO requestParamsDTO)
      throws CTPException, IOException {

    RequestType requestType = RequestType.AI_RH_POSTCODE;
    postcode = postcode.replaceAll("\\s", "");
    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

    // Hit AI and save results
    Object result = mockServiceClient.getAddressesRhPostcode(postcode);
    saveAiResponse(requestType, postcode, result);

    return result;
  }

  @RequestMapping(value = "/partial", method = RequestMethod.GET)
  public Object getAddressesPartial(@RequestParam(required = true) String input)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_PARTIAL;
    log.info("Request {}", requestType.getUrl(), kv("input", input));

    // Hit AI and save results
    Object result = mockServiceClient.getAddressesPartial(input);
    saveAiResponse(requestType, input, result);

    return result;
  }

  @RequestMapping(value = "/postcode/{postcode}", method = RequestMethod.GET)
  public Object getAddressesPostcode(@PathVariable(value = "postcode") String postcode)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_POSTCODE;
    postcode = postcode.replaceAll("\\s", "");
    log.info("Request {}/{}", requestType.getPath(), v("postcode", postcode));

    // Hit AI and save results
    Object result = mockServiceClient.getAddressesPostcode(postcode);
    saveAiResponse(requestType, postcode, result);

    return result;
  }

  @RequestMapping(value = "/rh/uprn/{uprn}", method = RequestMethod.GET)
  public Object getRhPostcode(@PathVariable(value = "uprn") String uprn)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_RH_UPRN;
    log.info("Request {}/{}", requestType.getPath(), v("uprn", uprn));

    // Hit AI and save results
    Object result = mockServiceClient.getAddressesRhUprn(uprn);
    saveAiResponse(requestType, uprn, result);

    return result;
  }

  @RequestMapping(value = "/eq", method = RequestMethod.GET)
  public Object getAddressesEq(@RequestParam(required = true) String input)
      throws IOException, CTPException {

    RequestType requestType = RequestType.AI_EQ;
    log.info("Request {}", requestType.getUrl(), kv("input", input));

    // Hit AI and save results
    Object result = mockServiceClient.getAddressesEq(input);
    saveAiResponse(requestType, input, result);

    return result;
  }

  private void saveAiResponse(RequestType requestType, String name, Object capturedResponse)
      throws IOException, CTPException {

    // Discover the location of the 'data' resource directory
    ClassLoader classLoader = getClass().getClassLoader();
    URL targetDataUrl = classLoader.getResource("data");
    File targetDataDir = new File(targetDataUrl.getFile());

    // Output json data to file in the compiled target file hierarchy (for immediate use)
    File targetCaptureDir = new File(targetDataDir, requestType.getPath());
    String outputFileName = DataRepository.normaliseFileName(name) + ".json";
    writeJsonFile(targetCaptureDir, outputFileName, capturedResponse, false);

    // Output json data to file in the source tree (for long term storage)
    File mockAiDir = targetDataDir.getParentFile().getParentFile().getParentFile();
    File srcDataDir = new File(mockAiDir, "src/main/resources/data");
    File srcCaptureDir = new File(srcDataDir, requestType.getPath());
    writeJsonFile(srcCaptureDir, outputFileName, capturedResponse, true);
  }

  private void writeJsonFile(
      File dir, String fileName, Object capturedResponse, boolean failIfNoTargetDir)
      throws CTPException, IOException {
    // Validate target directory
    if (!dir.exists()) {
      if (failIfNoTargetDir) {
        log.error("Output data directory does not exist", kv("output dir", dir.getAbsolutePath()));
        throw new CTPException(
            Fault.SYSTEM_ERROR, "Output data directory does not exist: " + dir.getAbsolutePath());
      } else {
        log.warn("Output data directory does not exist", kv("output dir", dir.getAbsolutePath()));
        return;
      }
    }

    // Convert object to json string
    String json;
    ObjectMapper mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    try {
        json = mapper.writeValueAsString(capturedResponse);
        System.out.println("Ready to write. Have formatted JSON String: " + json); 
    } catch (JsonGenerationException e) {
      throw new CTPException(Fault.SYSTEM_ERROR, "Failed to format json", e);
    } catch (JsonMappingException e) {
      throw new CTPException(Fault.SYSTEM_ERROR, "Failed to format json", e);
    } catch (IOException e) {
      throw new CTPException(Fault.SYSTEM_ERROR, "Failed to format json", e);
    }

    // Write AI response json to file
    File outputFile = new File(dir, fileName);
    log.info("Saving AI data to json file", kv("outputFile", outputFile.getAbsoluteFile()));
    Files.write(outputFile.toPath(), json.getBytes());
  }
}
