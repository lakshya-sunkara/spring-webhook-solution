package com.example.demo;

import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class WebhookService {

    // Use a RestTemplate instance (fine for this simple app)
    private final RestTemplate restTemplate = new RestTemplate();

    public void executeFlow() {
        System.out.println("Starting webhook flow...");

        // 1. Call generateWebhook
        Map<String, Object> resp = generateWebhook();
        if (resp == null) {
            System.err.println("generateWebhook returned null or failed.");
            return;
        }

        String webhook = (String) resp.get("webhook");
        String token = (String) resp.get("accessToken");

        System.out.println("Webhook: " + webhook);
        System.out.println("Token: " + (token == null ? "null" : "[RECEIVED]"));

        // 2. Put your final SQL query here (we'll replace once you give the question)
        String finalQuery = "SELECT * FROM your_table LIMIT 1;";

        // 3. Submit final query
        sendFinalQuery(webhook, token, finalQuery);

        System.out.println("Flow finished.");
    }

    private Map<String, Object> generateWebhook() {
        try {
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> body = Map.of(
                "name", "Your Name",
                "regNo", "22BCE9865",
                "email", "yourEmail@example.com"
            );

            ResponseEntity<Map> response = restTemplate.postForEntity(url, body, Map.class);
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody();
            } else {
                System.err.println("generateWebhook HTTP error: " + response.getStatusCode());
                return null;
            }
        } catch (Exception e) {
            System.err.println("generateWebhook exception: " + e.getMessage());
            return null;
        }
    }

    private void sendFinalQuery(String webhook, String token, String finalQuery) {
        try {
            String url = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

            HttpHeaders headers = new HttpHeaders();
            if (token != null) headers.set("Authorization", token);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, String> body = Map.of("finalQuery", finalQuery);

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
            System.out.println("submit response status: " + response.getStatusCode());
            System.out.println("submit response body: " + response.getBody());
        } catch (Exception e) {
            System.err.println("sendFinalQuery exception: " + e.getMessage());
        }
    }
}
