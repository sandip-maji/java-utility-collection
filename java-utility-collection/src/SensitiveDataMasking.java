import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SensitiveDataMasking {

    // List of sensitive field names
    private static final List<String> SENSITIVE_KEYS = List.of("password", "passcode", "code", "secret", "token");

    public static String processInput(String input) {
        // Build a regex pattern from the sensitive keys
        String sensitiveRegex = buildRegexFromKeys(SENSITIVE_KEYS);

        // Check if any sensitive key is present
        if (containsSensitiveData(input, sensitiveRegex)) {
            // Mask sensitive data
            return maskSensitiveData(input, sensitiveRegex);
        }
        return input;
    }

    public static String buildRegexFromKeys(List<String> keys) {
        // Join keys to form a regex group, e.g., (password|passcode|code)
        String joinedKeys = String.join("|", keys);
        return "(?i)(" + joinedKeys + ")\\s*[:=]\\s*([^,\\s]+)";
    }

    public static boolean containsSensitiveData(String input, String regex) {
        // Compile the regex pattern
        Pattern pattern = Pattern.compile(regex);
        // Check if the pattern matches any part of the input
        Matcher matcher = pattern.matcher(input);
        return matcher.find();
    }

    public static String maskSensitiveData(String input, String regex) {
        // Replace sensitive data with a masked version
        return input.replaceAll(regex, "$1: ****");
    }

    public static void main(String[] args) {
        // Sample inputs
        String input1 = "username=admin, password=mySecret123, url=http://example.com";
        String input2 = "apiKey=12345, passcode=67890, token=abcdef, dbPassword=superSecret";
        String input3 = "username=admin, url=http://example.com";

        // Process each input
        String result1 = processInput(input1);
        String result2 = processInput(input2);
        String result3 = processInput(input3);

        // Output results
        System.out.println("Original: " + input1);
        System.out.println("Processed: " + result1);
        System.out.println("Original: " + input2);
        System.out.println("Processed: " + result2);
        System.out.println("Original: " + input3);
        System.out.println("Processed: " + result3);
    }
}
