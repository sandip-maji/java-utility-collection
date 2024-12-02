import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

public class JsonMasking {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Masks sensitive data in a JSON string based on a list of sensitive keys.
     *
     * @param jsonString    the original JSON string
     * @param sensitiveKeys a list of keys whose values need to be masked
     * @return the JSON string with sensitive data masked
     */
    public static String maskSensitiveData(String jsonString, List<String> sensitiveKeys) {
        try {
            // Convert the JSON string into a JsonNode
            JsonNode rootNode = objectMapper.readTree(jsonString);

            // Mask sensitive fields recursively
            maskSensitiveFields(rootNode, sensitiveKeys);

            // Convert the modified JsonNode back to a pretty-printed JSON string
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(rootNode);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return jsonString; // Return the original string in case of errors
        }
    }

    /**
     * Recursively traverses and masks sensitive fields in a JSON node.
     *
     * @param node          the JSON node
     * @param sensitiveKeys a list of keys to mask
     */
    private static void maskSensitiveFields(JsonNode node, List<String> sensitiveKeys) {
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            objectNode.fieldNames().forEachRemaining(fieldName -> {
                if (sensitiveKeys.contains(fieldName)) {
                    // Mask the sensitive field with a placeholder
                    objectNode.put(fieldName, "****");
                } else {
                    // Recursively mask fields within the object
                    maskSensitiveFields(objectNode.get(fieldName), sensitiveKeys);
                }
            });
        } else if (node.isArray()) {
            node.forEach(element -> maskSensitiveFields(element, sensitiveKeys));
        }
    }

    public static void main(String[] args) {
        // List of sensitive keys
        List<String> sensitiveKeys = List.of("userId", "password", "token", "apiKey", "fileId");

        // Example JSON input
        String jsonInput = "";

        // Mask sensitive data
        String maskedJson = maskSensitiveData(jsonInput, sensitiveKeys);

        // Print masked JSON
        System.out.println(maskedJson);
    }
}
