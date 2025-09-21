package com.whereq.spark.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.spark.sql.SparkSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/spark/info")
@Tag(name = "Spark Info", description = "Spark session information endpoints")
public class SparkInfoController {

    private final SparkSession sparkSession;

    public SparkInfoController(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    @GetMapping
    @Operation(summary = "Get Spark session info", description = "Retrieve information about the current Spark session")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Spark info retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<Map<String, Object>> getSparkInfo() {
        return Mono.fromCallable(() -> {
            Map<String, Object> info = new HashMap<>();
            info.put("sparkVersion", sparkSession.version());
            info.put("appName", sparkSession.sparkContext().appName());
            info.put("master", sparkSession.sparkContext().master());
            info.put("applicationId", sparkSession.sparkContext().applicationId());
            info.put("uiWebUrl", sparkSession.sparkContext().uiWebUrl().isDefined() ?
                sparkSession.sparkContext().uiWebUrl().get() : "N/A");
            return info;
        });
    }
}