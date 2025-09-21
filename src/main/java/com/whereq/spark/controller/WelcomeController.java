package com.whereq.spark.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
public class WelcomeController {

    @GetMapping("/")
    public Mono<Map<String, Object>> welcome() {
        return Mono.fromCallable(() -> {
            Map<String, Object> response = new HashMap<>();
            response.put("service", "Spark as a Service");
            response.put("version", "1.0.0");
            response.put("status", "Running");
            response.put("endpoints", Map.of(
                "submit_job", "POST /api/spark/jobs",
                "get_job_status", "GET /api/spark/jobs/{jobId}",
                "list_jobs", "GET /api/spark/jobs",
                "spark_info", "GET /api/spark/info",
                "health", "GET /actuator/health",
                "api_docs", "GET /v3/api-docs",
                "swagger_ui", "GET /swagger-ui.html"
            ));
            return response;
        });
    }
}