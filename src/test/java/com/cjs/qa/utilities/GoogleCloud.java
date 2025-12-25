package com.cjs.qa.utilities;

import java.io.IOException;

import org.apache.logging.log4j.LogManager;

import com.google.cloud.secretmanager.v1.AccessSecretVersionResponse;
import com.google.cloud.secretmanager.v1.SecretManagerServiceClient;
import com.google.cloud.secretmanager.v1.SecretVersionName;
import com.google.protobuf.ByteString;

public final class GoogleCloud {

  private static final GuardedLogger LOG =
      new GuardedLogger(LogManager.getLogger(GoogleCloud.class));

  private GoogleCloud() {
    // Utility class - prevent instantiation
  }

  // public class SecretManagerExample {

  /**
   * Retrieves the value of a secret from Google Cloud Secret Manager.
   *
   * @param projectId The Google Cloud project ID (e.g., "cscharer").
   * @param secretId The ID of the secret (e.g., "AIRTABLE_API_KEY").
   * @return The secret value as a String.
   * @throws IOException If the client creation fails.
   */
  public static String getKeyValue(String projectId, String secretId) throws IOException {
    // Initialize the Secret Manager client. This client only needs to be created
    // once and can be reused for multiple requests.
    try (SecretManagerServiceClient client = SecretManagerServiceClient.create()) {
      // Build the resource name of the secret version, using "latest"
      // to access the most recent version.
      SecretVersionName secretVersionName = SecretVersionName.of(projectId, secretId, "latest");

      // Access the secret version.
      AccessSecretVersionResponse response = client.accessSecretVersion(secretVersionName);

      // Extract the payload (secret value) and decode it from ByteString to a UTF-8 String.
      ByteString payload = response.getPayload().getData();
      return payload.toStringUtf8();
    }
  }

  public static void main(String[] args) {
    // Replace with your actual project ID and secret ID.
    String projectId = "cscharer";
    String secretId = "AIRTABLE_API_KEY";

    try {
      String keyValue = getKeyValue(projectId, secretId);
      LOG.debug("Retrieved secret value for {}: {}", secretId, keyValue);
    } catch (IOException e) {
      LOG.error("Failed to access secret: {}", e.getMessage(), e);
    }
  }
}
