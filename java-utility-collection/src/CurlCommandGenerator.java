import java.util.Map;

public class CurlCommandGenerator {

    public static String generateCurl(String baseURI, String resourcePath, Map<String, String> reqHeader, 
                                      Map<String, String> queryParam, Map<String, String> formPrams, 
                                      Map<String, String> parameterNameValuePairs, String requestBody, 
                                      HttpMethod httpMethod, CertDetails certDetails, boolean isReqResPrint, 
                                      String proxyHost) {
        StringBuilder curlCommand = new StringBuilder("curl -X ");

        // Append HTTP method
        curlCommand.append(httpMethod.name()).append(" ");

        // Combine base URI and resource path
        String url = baseURI + resourcePath;

        // Append path parameters to URL
        if (parameterNameValuePairs != null && !parameterNameValuePairs.isEmpty()) {
            for (Map.Entry<String, String> entry : parameterNameValuePairs.entrySet()) {
                url = url.replace("{" + entry.getKey() + "}", entry.getValue());
            }
        }

        // Append query parameters to URL
        if (queryParam != null && !queryParam.isEmpty()) {
            StringBuilder queryParamString = new StringBuilder("?");
            for (Map.Entry<String, String> entry : queryParam.entrySet()) {
                queryParamString.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
            }
            // Remove the last '&'
            queryParamString.deleteCharAt(queryParamString.length() - 1);
            url += queryParamString.toString();
        }

        // Append URL
        curlCommand.append("\"").append(url).append("\" ");

        // Append headers
        if (reqHeader != null && !reqHeader.isEmpty()) {
            for (Map.Entry<String, String> entry : reqHeader.entrySet()) {
                curlCommand.append("-H \"").append(entry.getKey()).append(": ").append(entry.getValue()).append("\" ");
            }
        }

        // Append proxy host if specified
        if (proxyHost != null && !proxyHost.isEmpty()) {
            curlCommand.append("-x \"").append(proxyHost).append("\" ");
        }

        // Append form parameters
        if (formPrams != null && !formPrams.isEmpty()) {
            for (Map.Entry<String, String> entry : formPrams.entrySet()) {
                curlCommand.append("-F \"").append(entry.getKey()).append("=").append(entry.getValue()).append("\" ");
            }
        }

        // Append request body
        if (requestBody != null && !requestBody.isEmpty()) {
            curlCommand.append("-d \"").append(requestBody.replace("\"", "\\\"")).append("\" ");
        }

        // Optionally print the request and response
        if (isReqResPrint) {
            System.out.println("Generated cURL Command: " + curlCommand.toString().trim());
        }

        return curlCommand.toString().trim();
    }

    public static void main(String[] args) {
        String baseURI = "https://api.example.com";
        String resourcePath = "/resource/{id}";

        Map<String, String> reqHeader = Map.of(
            "Content-Type", "application/json",
            "Authorization", "Bearer token"
        );

        Map<String, String> queryParam = Map.of(
            "search", "test",
            "limit", "10"
        );

        Map<String, String> formPrams = Map.of(
            "file", "@/path/to/file.txt"
        );

        Map<String, String> parameterNameValuePairs = Map.of(
            "id", "12345"
        );

        String requestBody = "{\"key\":\"value\"}";
        HttpMethod httpMethod = HttpMethod.POST;
        CertDetails certDetails = new CertDetails(); // Populate cert details as needed
        boolean isReqResPrint = true;
        String proxyHost = "http://proxy.example.com:8080";

        String curlCommand = generateCurl(baseURI, resourcePath, reqHeader, queryParam, formPrams, 
                                          parameterNameValuePairs, requestBody, httpMethod, 
                                          certDetails, isReqResPrint, proxyHost);
        System.out.println(curlCommand);
    }
}
