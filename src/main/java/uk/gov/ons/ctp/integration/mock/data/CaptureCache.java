package uk.gov.ons.ctp.integration.mock.data;

import static uk.gov.ons.ctp.common.log.ScopedStructuredArguments.kv;

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
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.integration.mock.endpoints.RequestType;
import uk.gov.ons.ctp.integration.mock.misc.Constants;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CaptureCache {

  public static String readCapturedAiResponse(RequestType requestType, String baseFileName)
      throws IOException, CTPException {

    // Work out where the captured data lives
    ClassLoader classLoader = CaptureCache.class.getClassLoader();
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
  public static List<String> listCapturedData(RequestType requestType) throws IOException {

    // Find all data files for the request type
    PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
    String resourcePath = "classpath*:data" + requestType.getPath() + "/*.json";
    Resource[] jsonFiles = resolver.getResources(resourcePath);

    // Build list of file names
    ArrayList<String> fileNames = new ArrayList<>();
    for (Resource resource : jsonFiles) {
      String fileName = resource.getFilename();
      if (!fileName.startsWith(Constants.INTERNAL_FILE_NAME_PREFIX)) {
        fileNames.add(CaptureCache.denormaliseFileName(fileName));
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
  private static File getRequestTypeCaptureDir(RequestType requestType) {
    String requestDataDir = "data" + requestType.getPath();
    ClassLoader classLoader = CaptureCache.class.getClassLoader();
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
    File resourceDir = getRequestTypeCaptureDir(requestType);
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
    String cleanedName = trimmedName.replaceAll(" ", "-");

    return cleanedName;
  }

  private static String denormaliseFileName(String name) {
    String baseName = name.replace(".json", "");
    String originalName = baseName.replaceAll("-", " ");

    return originalName;
  }
}
