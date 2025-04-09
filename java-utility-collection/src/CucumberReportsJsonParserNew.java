import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class CucumberReportParser {

    public static void main(String[] args) throws IOException {
        String content = Files.readString(Paths.get("target/cucumber/report.js"));
        Map<String, Object> parsed = parseReport(content);
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(parsed));
    }

    public static Map<String, Object> parseReport(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        List<String> lines = Arrays.asList(content.split("\n"));
        Map<String, Object> report = new LinkedHashMap<>();
        List<Map<String, Object>> features = new ArrayList<>();
        Map<String, Object> currentFeature = null;
        Map<String, Object> currentScenario = null;
        List<Map<String, Object>> currentScenarios = null;
        List<Map<String, Object>> currentSteps = null;

        StringBuilder jsonBuffer = new StringBuilder();
        String currentType = null;
        boolean inBlock = false;

        for (String line : lines) {
            line = line.trim();

            // Start of a formatter call
            if (line.startsWith("formatter.")) {
                int start = line.indexOf("formatter.") + 10;
                int parenIndex = line.indexOf("(", start);
                currentType = line.substring(start, parenIndex);
                inBlock = true;
                jsonBuffer.setLength(0); // clear
                line = line.substring(parenIndex + 1);
            }

            if (inBlock) {
                jsonBuffer.append(line);

                if (line.endsWith("});") || line.endsWith("} );")) {
                    inBlock = false;
                    String jsonStr = jsonBuffer.toString();
                    jsonStr = jsonStr.replaceAll("\\);$", "").trim();
                    JsonNode jsonNode = objectMapper.readTree(jsonStr);

                    switch (currentType) {
                        case "feature":
                            currentFeature = new LinkedHashMap<>();
                            currentFeature.put("name", jsonNode.get("name").asText());
                            currentScenarios = new ArrayList<>();
                            currentFeature.put("scenarios", currentScenarios);
                            features.add(currentFeature);
                            break;
                        case "scenario":
                        case "scenarioOutline":
                            currentScenario = new LinkedHashMap<>();
                            currentScenario.put("name", jsonNode.get("name").asText());
                            currentScenario.put("type", currentType.equals("scenarioOutline") ? "Scenario Outline" : "Scenario");
                            currentSteps = new ArrayList<>();
                            currentScenario.put("steps", currentSteps);
                            if (currentScenarios != null) {
                                currentScenarios.add(currentScenario);
                            }
                            break;
                        case "step":
                            if (currentSteps != null && jsonNode.has("name") && jsonNode.has("keyword")) {
                                Map<String, Object> step = new LinkedHashMap<>();
                                step.put("name", jsonNode.get("name").asText());
                                step.put("keyword", jsonNode.get("keyword").asText());
                                currentSteps.add(step);
                            }
                            break;
                        case "result":
                            if (currentSteps != null && !currentSteps.isEmpty()) {
                                Map<String, Object> lastStep = currentSteps.get(currentSteps.size() - 1);
                                lastStep.put("status", jsonNode.get("status").asText());
                            }
                            break;
                        default:
                            // Skip unknown types
                            break;
                    }
                }
            }
        }

        report.put("features", features);
        return report;
    }
}