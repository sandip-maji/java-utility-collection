import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;

public class CucumberReportJsonParser {

    public static void main(String[] args) throws IOException {
        String content = Files.readString(Paths.get("target/cucumber/report.js"));
        Map<String, Object> parsedReport = parseReport(content);
        System.out.println(new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(parsedReport));
    }

    public static Map<String, Object> parseReport(String content) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

Pattern pattern = Pattern.compile("formatter\\.(\\w+)\(\\{.*?\\})\;", Pattern.DOTALL);

        //Pattern pattern = Pattern.compile("formatter\\.(\\w+)\(\\{.*?\\})\;", Pattern.DOTALL);


        Matcher matcher = pattern.matcher(content);

        Map<String, Object> report = new LinkedHashMap<>();
        List<Map<String, Object>> features = new ArrayList<>();
        Map<String, Object> currentFeature = null;
        Map<String, Object> currentScenario = null;
        List<Map<String, Object>> currentScenarios = null;
        List<Map<String, Object>> currentSteps = null;

        while (matcher.find()) {
            String type = matcher.group(1);
            String jsonText = matcher.group(2).replaceAll("\\\\u([0-9A-Fa-f]{4})", match -> {
                int code = Integer.parseInt(match.group(1), 16);
                return String.valueOf((char) code);
            });

            JsonNode jsonNode = objectMapper.readTree(jsonText);

            switch (type) {
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
                    currentScenario.put("type", type.equals("scenarioOutline") ? "Scenario Outline" : "Scenario");
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
                    // Ignore other types like uri, examples, etc. for now
                    break;
            }
        }

        report.put("features", features);
        return report;
    }
}