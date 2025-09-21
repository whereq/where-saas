package com.whereq.spark.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Map;

@Schema(description = "Request object for submitting a Spark job")
public record JobRequest(
        @Schema(description = "Name of the job", example = "daily-sales-report", required = true)
        @NotBlank String jobName,

        @Schema(description = "Type of the job", example = "SQL_QUERY", required = true)
        @NotNull JobType jobType,

        @Schema(description = "SQL query or code to execute",
                example = "SELECT COUNT(*) as total FROM sales WHERE date = '2023-01-01'",
                required = true)
        @NotBlank String sql,

        @Schema(description = "Additional parameters for job execution",
                example = "{\"spark.sql.adaptive.enabled\": \"true\"}")
        Map<String, String> parameters
) {
    @Schema(description = "Available job types")
    public enum JobType {
        @Schema(description = "Execute SQL query")
        SQL_QUERY,
        @Schema(description = "Run batch processing job")
        BATCH_PROCESSING,
        @Schema(description = "Run streaming job")
        STREAMING
    }
}