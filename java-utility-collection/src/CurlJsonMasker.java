import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CurlJsonMasker {

    // List of keys to mask
    static List<String> keysToMask = List.of("userAccountId", "firstName", "lastName", "workTypes");

    // Method to mask the value of specified keys in the JSON
    public static JsonNode maskJsonNode(JsonNode node, List<String> keysToMask) {
        if (node.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> field = fields.next();
                if (keysToMask.contains(field.getKey())) {
                    // Mask the value of the specified key
                    ((ObjectNode) node).put(field.getKey(), "****");
                } else {
                    // Recurse into nested objects
                    maskJsonNode(field.getValue(), keysToMask);
                }
            }
        } else if (node.isArray()) {
            for (JsonNode element : node) {
                // Recurse into array elements
                maskJsonNode(element, keysToMask);
            }
        }
        return node;
    }

    // Method to extract JSON data from cURL command (Regex)
    public static String extractJsonFromCurl(String curlCommand) {
        Pattern pattern = Pattern.compile("--data '(.*?)'", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(curlCommand);
        if (matcher.find()) {
            return matcher.group(1); // Return the JSON part inside the single quotes
        }
        return null;
    }

    // Method to rebuild the cURL command with masked JSON
    public static String rebuildCurlCommand(String originalCurlCommand, String maskedJsonString) {
        // Replace the original JSON in the cURL command with the masked JSON
        return originalCurlCommand.replaceFirst("(?<=--data ').*?(?=')", maskedJsonString);
    }

    // Method to parse the cURL command, mask values for multiple keys, and return the full cURL command
    public static String maskCurlJson(String curlCommand, List<String> keysToMask) throws Exception {
        String jsonString = extractJsonFromCurl(curlCommand);

        if (jsonString == null) {
            return "No JSON data found in the provided cURL command.";
        }

        // Use Jackson to parse the JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(jsonString);

        // Mask values for the provided keys
        JsonNode maskedNode = maskJsonNode(jsonNode, keysToMask);

        // Convert the modified JSON back to string
        String maskedJsonString = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(maskedNode);

        // Rebuild the cURL command with the masked JSON
        return rebuildCurlCommand(curlCommand, maskedJsonString);
    }

    public static void main(String[] args) {
        try {
            // Example cURL command
            String curlCommand = "";

            // Mask the JSON in the cURL command
            String maskedCurl = maskCurlJson(curlCommand, keysToMask);

            // Print the masked cURL command
            System.out.println(maskedCurl);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
