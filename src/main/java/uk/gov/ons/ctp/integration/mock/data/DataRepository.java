package uk.gov.ons.ctp.integration.mock.data;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mock.Constants;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPartialAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPartialResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexRhPostcodeAddressDTO;
import uk.gov.ons.ctp.integration.mock.ai.model.AddressIndexRhPostcodeResultsDTO;
import uk.gov.ons.ctp.integration.mock.endpoint.RequestType;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataRepository {

  public static String read(RequestType requestType, String name) throws IOException, CTPException {
    String baseFileName = normaliseFileName(name);

    // Work out where the captured data lives
    ClassLoader classLoader = DataRepository.class.getClassLoader();
    String dataFile = "data" + requestType.getPath() + "/" + baseFileName + ".json";

    // Return nothing if data not held for request
    URL resource = classLoader.getResource(dataFile);
    if (resource == null) {
      log.info(
          "No captured response",
          kv("baseFileName", baseFileName),
          kv("resource", dataFile),
          kv("requestType", requestType.name()));
      return null;
    }

    // Read AI captured response
    InputStream targetDataUrl = resource.openStream();
    StringBuilder responseBuilder = new StringBuilder();
    try (Reader reader =
        new BufferedReader(
            new InputStreamReader(targetDataUrl, Charset.forName(StandardCharsets.UTF_8.name())))) {
      int c = 0;
      while ((c = reader.read()) != -1) {
        responseBuilder.append((char) c);
      }
    }

    return responseBuilder.toString();
  }

  /**
   * Returns a list of the file names held for a type of request.
   *
   * @param requestType is the request to list the names for.
   * @return A List containing the names of the files.
   * @throws IOException if something went wrong.
   */
  public static List<String> list(RequestType requestType) throws IOException {

    // Find all data files for the request type
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    String resourcePath = "classpath*:data" + requestType.getPath() + "/*.json";
    Resource[] jsonFiles = resolver.getResources(resourcePath);

    // Build list of file names
    ArrayList<String> fileNames = new ArrayList<>();
    for (Resource resource : jsonFiles) {
      String fileName = resource.getFilename();
      if (!fileName.startsWith(Constants.INTERNAL_FILE_NAME_PREFIX)) {
        fileNames.add(denormaliseFileName(fileName));
      }
    }

    // Sort names so that longer versions appear first. Eg, 'London' would be listed before 'Londo'
    Collections.sort(
        fileNames,
        new Comparator<String>() {
          public int compare(String name1, String name2) {
            if (name1.startsWith(name2) || name2.startsWith(name1)) {
              return name2.length() - name1.length();
            }

            return name1.compareTo(name2);
          }
        });

    return fileNames;
  }

  // Work out directory name for requests files
  private static File folder(RequestType requestType) {
    String requestDataDir = "data" + requestType.getPath();
    ClassLoader classLoader = DataRepository.class.getClassLoader();
    File resourceDir = new File(classLoader.getResource(requestDataDir).getFile());
    return resourceDir;
  }

  /**
   * If the data directory for the specified requestType contains an inventory property file then
   * this method loads it and returns the contents as properties. The optional inventory file is
   * used to supply a description for key data files in the generated help text.
   *
   * @param requestType is the type of request to load the inventory for.
   * @return The Properties specified in the inventory file.
   * @throws IOException If the was a failure to read the file.
   * @throws FileNotFoundException Should not happen.
   */
  public static Properties getInventory(RequestType requestType)
      throws FileNotFoundException, IOException {
    // Work out path to inventory file
    File resourceDir = folder(requestType);
    File inventoryFile = new File(resourceDir, Constants.INVENTORY_FILE_NAME);

    // Load the inventory data file
    Properties prop = new Properties();
    if (inventoryFile.exists()) {
      prop.load(new FileInputStream(inventoryFile));
    }

    return prop;
  }

  public static String normaliseFileName(String name) {
    String trimmedName = name.trim();
    return trimmedName.replaceAll(" ", "-").toLowerCase();
  }

  private static String denormaliseFileName(String name) {
    String baseName = name.replace(".json", "");
    return baseName.replaceAll("-", " ");
  }

  @SuppressWarnings("unchecked")
  public static ResponseEntity<Object> simulateResponse(
      RequestType requestType, String name, int offset, int limit)
      throws IOException, CTPException {

    HttpStatus responseStatus = HttpStatus.OK;
    Object response = null;
    String responseText = DataRepository.read(requestType, name);

    if (responseText != null) {
      // Convert captured AI response to an object, and return the target subset of data
      if (requestType.getResponseClass().equals(String.class)) {
        // Don't push it through Jackson, as it is already in String format
        response = responseText;
      } else {
        response =
            new ObjectMapper().readerFor(requestType.getResponseClass()).readValue(responseText);
      }

      switch (requestType) {
        case AI_RH_POSTCODE:
          AddressIndexRhPostcodeResultsDTO rhPostcodes =
              (AddressIndexRhPostcodeResultsDTO) response;
          List<AddressIndexRhPostcodeAddressDTO> rhPostcodeAddresses =
              (List<AddressIndexRhPostcodeAddressDTO>)
                  subset(rhPostcodes.getResponse().getAddresses(), offset, limit);
          rhPostcodes.getResponse().setAddresses(rhPostcodeAddresses);
          rhPostcodes.getResponse().setOffset(offset);
          rhPostcodes.getResponse().setLimit(limit);
          // Replicate the counting down of the confidence score
          int confidence = 100000 + rhPostcodeAddresses.size();
          for (AddressIndexRhPostcodeAddressDTO address : rhPostcodeAddresses) {
            address.setConfidenceScore(confidence--);
          }
          break;
        case AI_PARTIAL:
          AddressIndexPartialResultsDTO partial = (AddressIndexPartialResultsDTO) response;
          List<AddressIndexPartialAddressDTO> partialAddresses =
              (List<AddressIndexPartialAddressDTO>)
                  subset(partial.getResponse().getAddresses(), offset, limit);
          partial.getResponse().setAddresses(partialAddresses);
          partial.getResponse().setOffset(offset);
          partial.getResponse().setLimit(limit);
          break;
        case AI_POSTCODE:
          AddressIndexPostcodeResultsDTO postcodes = (AddressIndexPostcodeResultsDTO) response;
          List<AddressIndexPostcodeAddressDTO> postcodeAddresses =
              (List<AddressIndexPostcodeAddressDTO>)
                  subset(postcodes.getResponse().getAddresses(), offset, limit);
          postcodes.getResponse().setAddresses(postcodeAddresses);
          postcodes.getResponse().setOffset(offset);
          postcodes.getResponse().setLimit(limit);
        case AI_EQ:
        case AI_RH_UPRN:
        case CASE_UPRN:
        case CASE_ID:
        case CASE_QID:
        case CASE_REF:
          // Nothing to do these types
          break;
        default:
          throw new CTPException(
              CTPException.Fault.SYSTEM_ERROR, "Unrecognised request type: " + requestType.name());
      }
    } else {
      // 404 - not found
      responseStatus = requestType.getNotFoundHttpStatus();
      responseText = DataRepository.read(requestType, Constants.NO_DATA_FILE_NAME);

      // Customise the not-found response by replacing any place holders with actual values
      String placeholderName = requestType.getPlaceholderName();
      if (placeholderName != null) {
        String fullPlaceholderName = "%" + placeholderName + "%";
        responseText = responseText.replace(fullPlaceholderName, name);
      }
      response = responseText;
    }
    return new ResponseEntity<Object>(response, responseStatus);
  }

  private static List<?> subset(List<?> addresses, int offset, int limit) {
    if (offset > addresses.size() || offset < 0) {
      return new ArrayList<Object>();
    }
    int toIndex = Math.min(offset + limit, addresses.size());
    return addresses.subList(offset, toIndex);
  }
}
