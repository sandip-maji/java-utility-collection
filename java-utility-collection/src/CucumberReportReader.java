import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.*;

public class CucumberReportReader {

    public BDDReportResponse getBDDReport(String pathToJsonFile) {
        BDDReportResponse response = new BDDReportResponse();
        List<FeatureResult> featureResults = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootArray = objectMapper.readTree(new File(pathToJsonFile));

            for (JsonNode featureNode : rootArray) {
                JsonNode scenarios = featureNode.get("elements");

                if (scenarios != null) {
                    for (JsonNode scenarioNode : scenarios) {
                        FeatureResult featureResult = new FeatureResult();
                        featureResult.setScenarioName(scenarioNode.get("name").asText());

                        // Determine overall scenario status
                        boolean allPassed = true;
                        for (JsonNode step : scenarioNode.get("steps")) {
                            String stepStatus = step.get("result").get("status").asText();
                            if (!"passed".equalsIgnoreCase(stepStatus)) {
                                allPassed = false;
                                break;
                            }
                        }
                        featureResult.setStatus(allPassed ? "pass" : "fail");

                        // Extract tags
                        List<String> tags = new ArrayList<>();
                        JsonNode tagNodes = scenarioNode.get("tags");
                        if (tagNodes != null) {
                            for (JsonNode tag : tagNodes) {
                                tags.add(tag.get("name").asText());
                            }
                        }
                        featureResult.setTags(tags);

                        featureResults.add(featureResult);
                    }
                }
            }

            response.setFeatures(featureResults);
        } catch (Exception e) {
            e.printStackTrace(); // Handle properly in production
        }

        return response;
    }
}
