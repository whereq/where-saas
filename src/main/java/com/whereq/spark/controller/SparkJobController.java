package com.whereq.spark.controller;

import com.whereq.spark.model.JobRequest;
import com.whereq.spark.model.JobResponse;
import com.whereq.spark.service.SparkJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/api/spark/jobs")
@Tag(name = "Spark Jobs", description = "Spark job management endpoints")
public class SparkJobController {

    private final SparkJobService sparkJobService;

    public SparkJobController(SparkJobService sparkJobService) {
        this.sparkJobService = sparkJobService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    @Operation(summary = "Submit a new Spark job", description = "Submit a new Spark job for execution")
    @ApiResponses({
        @ApiResponse(responseCode = "202", description = "Job submitted successfully",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid job request"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<JobResponse> submitJob(
            @Parameter(description = "Job request details", required = true)
            @Valid @RequestBody JobRequest request) {
        return sparkJobService.submitJob(request);
    }

    @GetMapping("/{jobId}")
    @Operation(summary = "Get job status", description = "Retrieve the status and details of a specific job")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Job found",
                    content = @Content(schema = @Schema(implementation = JobResponse.class))),
        @ApiResponse(responseCode = "404", description = "Job not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<JobResponse> getJobStatus(
            @Parameter(description = "Job ID", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
            @PathVariable String jobId) {
        return sparkJobService.getJobStatus(jobId);
    }

    @GetMapping
    @Operation(summary = "List all jobs", description = "Retrieve a list of all submitted jobs")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Jobs retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public Mono<List<JobResponse>> getAllJobs() {
        return sparkJobService.getAllJobs();
    }
}