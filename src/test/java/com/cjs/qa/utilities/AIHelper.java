package com.cjs.qa.utilities;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

public final class AIHelper {

  private AIHelper() {
    // Utility class - prevent instantiation
  }

  private static final GuardedLogger LOG = new GuardedLogger(LogManager.getLogger(AIHelper.class));

  private static final Gson GSON = new Gson();
  private static final String BASE_URL = "http://localhost:11434/v1";

  public static void main(String[] args) throws IOException, InterruptedException {
    // Load environment variables (Java handles this via System.getenv())
    // Note: For complex .env file loading, you might use a library like 'dotenv-java'
    String apiKey = Optional.ofNullable(System.getenv("OLLAMA_API_KEY")).orElse("ollama");
    String model = "llama3.2";
    String forTool = "Selenium";
    String setSystemPrompt = "You are a highly skilled software developer.";
    String exampleLocatorJson =
        """
      {
          "locators": [
              {
                  "name": "Label Loading",
                  "type": "xpath",
                  "value": ".//*[@id='cphBody_divLoader']//div[@class='loader-text']",
                  "variableName": "labelLoading"
              },
              {
                  "name": "Edit First Name",
                  "type": "id",
                  "value": "txtFirst",
                  "variableName": "editFirstName"
              },
              {
                  "name": "Edit Last Name",
                  "type": "name",
                  "value": "txtLast",
                  "variableName": "editLastName"
              }
          ]
      }
        """;

    String code =
        """
      package com.cjs.qa.americanairlines.pages;

      import com.cjs.qa.americanairlines.AmericanAirlinesEnvironment;
      import com.cjs.qa.core.Environment;
      import com.cjs.qa.core.QAException;
      import com.cjs.qa.selenium.Page;
      import org.openqa.selenium.By;
      import org.openqa.selenium.WebDriver;

            public class VacationabilityPage extends Page {
        public VacationabilityPage(WebDriver webDriver) {
          super(webDriver);
        }

        private int searches = 180;
        private static final By labelLoading =
            By.xpath(".//*[@id='cphBody_divLoader']//div[@class='loader-text']");
        private static final By editFirstName = By.xpath(".//*[@id='txtFirst']");
        private static final By editLastName = By.xpath(".//*[@id='txtLast']");
        private static final By editEmail = By.xpath(".//*[@id='txtEmail']");
        private static final By editAANumber = By.xpath(".//*[@id='txtAANumber']");
        private static final By checkboxAgree =
            By.xpath(".//*[@id='divRegistration']//label[@for='chkAgree']/span");
        private static final By buttonRegister = By.xpath(".//*[@id='btnRegister']");
        private static final By labelThankYou =
            By.xpath(".//*[@id='cphBody_divStaticPage']//div[@class='box-title-copy']");

        private int getSearches() {
          return searches;
        }

        private void setSearches(int searches) {
          this.searches = searches;
        }
      }
        """;

    String userPrompt =
        String.format(
            "Find all the locators in this code and produce a JSON with them for use with %s.\n"
                + "Note: All locators are defined with = By.\n"
                + "Only return the json and the variable name and nothing more.\n"
                + "Include locators even if they are not being used.\n"
                + "This is an example of what the json should look like:\n%s\n"
                + "This is the code:\n%s",
            forTool, exampleLocatorJson, code);

    // Build the request payload
    List<Map<String, String>> messages =
        Arrays.asList(
            Map.of("role", "system", "content", setSystemPrompt),
            Map.of("role", "user", "content", userPrompt));

    ChatCompletionRequest requestBody = new ChatCompletionRequest(model, messages, 1000, 0.7);
    String jsonPayload = GSON.toJson(requestBody);

    // Use the built-in HttpClient to send the request
    try (HttpClient client = HttpClient.newHttpClient()) {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(BASE_URL + "/chat/completions"))
              .header("Content-Type", "application/json")
              .header("Authorization", "Bearer " + apiKey)
              .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      // Process the response
      if (response.statusCode() == 200) {
        ChatCompletionResponse completionResponse =
            GSON.fromJson(response.body(), ChatCompletionResponse.class);
        if (completionResponse.getChoices() != null && !completionResponse.getChoices().isEmpty()) {
          LOG.debug("{}", completionResponse.getChoices().get(0).getMessage().getContent());
        }
      } else {
        LOG.error("Request failed with status code: {}", response.statusCode());
        LOG.error("Response body: {}", response.body());
      }
    }
  }

  // --- Data Classes for JSON Serialization/Deserialization ---

  static class ChatCompletionRequest {

    private String model;
    private List<Map<String, String>> messages;

    @SerializedName("max_tokens")
    private int maxTokens;

    private double temperature;

    // No-arg constructor for Gson deserialization
    @SuppressWarnings("unused")
    public ChatCompletionRequest() {
      // Empty constructor required for Gson deserialization
    }

    public ChatCompletionRequest(
        String model, List<Map<String, String>> messages, int maxTokens, double temperature) {
      this.model = model;
      this.messages = messages;
      this.maxTokens = maxTokens;
      this.temperature = temperature;
    }

    public String getModel() {
      return model;
    }

    public List<Map<String, String>> getMessages() {
      return messages;
    }

    public int getMaxTokens() {
      return maxTokens;
    }

    public double getTemperature() {
      return temperature;
    }
  }

  static class ChatCompletionResponse {

    private List<Choice> choices;

    // Other fields like id, object, created can be added if needed

    // No-arg constructor for Gson deserialization
    @SuppressWarnings("unused")
    public ChatCompletionResponse() {
      // Empty constructor required for Gson deserialization
    }

    public List<Choice> getChoices() {
      return choices;
    }
  }

  static class Choice {

    private Message message;

    // Other fields like index, logprobs, finish_reason can be added

    // No-arg constructor for Gson deserialization
    @SuppressWarnings("unused")
    public Choice() {
      // Empty constructor required for Gson deserialization
    }

    public Message getMessage() {
      return message;
    }
  }

  static class Message {

    private String role;
    private String content;

    // No-arg constructor for Gson deserialization
    @SuppressWarnings("unused")
    public Message() {
      // Empty constructor required for Gson deserialization
    }

    public String getRole() {
      return role;
    }

    public String getContent() {
      return content;
    }
  }
}
