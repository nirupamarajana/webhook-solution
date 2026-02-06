package com.bajaj.webhook;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class WebhookSolutionApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebhookSolutionApplication.class, args);
    }

    @Bean
    public CommandLineRunner run() {
        return args -> {

            RestTemplate restTemplate = new RestTemplate();

            String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("name", "Nirupama Rajana");
            requestBody.put("regNo", "250850120112");
            requestBody.put("email", "your-email@example.com");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request =
                    new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(generateUrl, request, Map.class);

            Map<String, String> responseBody = response.getBody();

            String webhookUrl = responseBody.get("webhook");
            String accessToken = responseBody.get("accessToken");

            System.out.println("Webhook URL: " + webhookUrl);
            System.out.println("Access Token: " + accessToken);

            String finalQuery =
                    "SELECT d.DEPARTMENT_NAME, " +
                    "ROUND(AVG(TIMESTAMPDIFF(YEAR, e.DOB, CURDATE())),2) AS AVERAGE_AGE, " +
                    "SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) " +
                    "ORDER BY e.EMP_ID SEPARATOR ', '), ', ', 10) AS EMPLOYEE_LIST " +
                    "FROM EMPLOYEE e " +
                    "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                    "JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID " +
                    "WHERE p.AMOUNT > 70000 " +
                    "GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
                    "ORDER BY d.DEPARTMENT_ID DESC;";

            Map<String, String> resultBody = new HashMap<>();
            resultBody.put("finalQuery", finalQuery);

            HttpHeaders authHeaders = new HttpHeaders();
            authHeaders.setContentType(MediaType.APPLICATION_JSON);
            authHeaders.set("Authorization", accessToken);

            HttpEntity<Map<String, String>> resultRequest =
                    new HttpEntity<>(resultBody, authHeaders);

            ResponseEntity<String> resultResponse =
                    restTemplate.postForEntity(webhookUrl, resultRequest, String.class);

            System.out.println("Submission response: " + resultResponse.getBody());
        };
    }
}
