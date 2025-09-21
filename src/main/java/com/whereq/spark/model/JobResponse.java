package com.whereq.spark.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Schema(description = "Response object containing job execution details")
public record JobResponse(
        @Schema(description = "Unique job identifier", example = "550e8400-e29b-41d4-a716-446655440000")
        String jobId,

        @Schema(description = "Name of the job", example = "daily-sales-report")
        String jobName,

        @Schema(description = "Current status of the job")
        JobStatus status,

        @Schema(description = "Job start timestamp", example = "2023-01-01T10:00:00")
        LocalDateTime startTime,

        @Schema(description = "Job completion timestamp", example = "2023-01-01T10:05:30")
        LocalDateTime endTime,

        @Schema(description = "Job execution results (only for completed jobs)")
        List<Map<String, Object>> result,

        @Schema(description = "Error message (only for failed jobs)")
        String errorMessage
) {
    @Schema(description = "Available job statuses")
    public enum JobStatus {
        @Schema(description = "Job has been submitted and queued")
        SUBMITTED,
        @Schema(description = "Job is currently running")
        RUNNING,
        @Schema(description = "Job completed successfully")
        COMPLETED,
        @Schema(description = "Job failed with an error")
        FAILED
    }
}