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
            FeatureResult featureResult = new FeatureResult();
            featureResult.setFeatureName(featureNode.get("name").asText());
            featureResult.setUrl(featureNode.has("uri") ? featureNode.get("uri").asText() : null);

            List<ScenarioResult> scenarioResults = new ArrayList<>();
            boolean allScenariosPassed = true;
            Set<String> featureTagsSet = new HashSet<>();

            JsonNode scenarios = featureNode.get("elements");
            if (scenarios != null) {
                for (JsonNode scenarioNode : scenarios) {
                    ScenarioResult scenarioResult = new ScenarioResult();
                    scenarioResult.setScenarioName(scenarioNode.get("name").asText());

                    List<String> scenarioTags = new ArrayList<>();
                    JsonNode tagNodes = scenarioNode.get("tags");
                    if (tagNodes != null) {
                        for (JsonNode tag : tagNodes) {
                            String tagName = tag.get("name").asText();
                            scenarioTags.add(tagName);
                            featureTagsSet.add(tagName); // add to feature-level tags
                        }
                    }
                    scenarioResult.setTags(scenarioTags);

                    List<Steps> steps = new ArrayList<>();
                    boolean allStepsPassed = true;

                    for (JsonNode stepNode : scenarioNode.get("steps")) {
                        Steps step = new Steps();
                        String keyword = stepNode.get("keyword").asText();
                        String stepName = stepNode.get("name").asText();
                        String status = stepNode.get("result").get("status").asText();

                        step.setName(keyword + stepName);
                        step.setStatus(status);
                        steps.add(step);

                        if (!"passed".equalsIgnoreCase(status)) {
                            allStepsPassed = false;
                        }
                    }

                    scenarioResult.setSteps(steps);
                    scenarioResult.setStatus(allStepsPassed ? "pass" : "fail");

                    if (!allStepsPassed) {
                        allScenariosPassed = false;
                    }

                    scenarioResults.add(scenarioResult);
                }
            }

            featureResult.setTags(new ArrayList<>(featureTagsSet));
            featureResult.setScenarios(scenarioResults);
            featureResult.setStatus(allScenariosPassed ? "pass" : "fail");

            featureResults.add(featureResult);
        }

        response.setFeatures(featureResults);
    } catch (Exception e) {
        e.printStackTrace();
    }

    return response;
}

}
