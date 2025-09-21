package com.whereq.spark.service;

import com.whereq.spark.model.JobRequest;
import com.whereq.spark.model.JobResponse;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SparkJobService {

    private static final Logger logger = LoggerFactory.getLogger(SparkJobService.class);

    private final SparkSession sparkSession;
    private final Map<String, JobResponse> jobs = new ConcurrentHashMap<>();

    public SparkJobService(SparkSession sparkSession) {
        this.sparkSession = sparkSession;
    }

    public Mono<JobResponse> submitJob(JobRequest request) {
        String jobId = UUID.randomUUID().toString();

        JobResponse initialResponse = new JobResponse(
                jobId,
                request.jobName(),
                JobResponse.JobStatus.SUBMITTED,
                LocalDateTime.now(),
                null,
                null,
                null
        );

        jobs.put(jobId, initialResponse);

        return Mono.fromCallable(() -> executeJob(jobId, request))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(s -> {
                    JobResponse runningJob = new JobResponse(
                            jobId,
                            request.jobName(),
                            JobResponse.JobStatus.RUNNING,
                            initialResponse.startTime(),
                            null,
                            null,
                            null
                    );
                    jobs.put(jobId, runningJob);
                    logger.info("Job {} started execution", jobId);
                })
                .onErrorResume(throwable -> {
                    JobResponse failedJob = new JobResponse(
                            jobId,
                            request.jobName(),
                            JobResponse.JobStatus.FAILED,
                            initialResponse.startTime(),
                            LocalDateTime.now(),
                            null,
                            throwable.getMessage()
                    );
                    jobs.put(jobId, failedJob);
                    logger.error("Job {} failed", jobId, throwable);
                    return Mono.just(failedJob);
                });
    }

    public Mono<JobResponse> getJobStatus(String jobId) {
        return Mono.fromCallable(() -> jobs.get(jobId))
                .switchIfEmpty(Mono.error(new RuntimeException("Job not found: " + jobId)));
    }

    public Mono<List<JobResponse>> getAllJobs() {
        return Mono.fromCallable(() -> new ArrayList<>(jobs.values()));
    }

    private JobResponse executeJob(String jobId, JobRequest request) {
        try {
            logger.info("Executing job {}: {}", jobId, request.jobName());

            List<Map<String, Object>> result = switch (request.jobType()) {
                case SQL_QUERY -> executeSqlQuery(request.sql());
                case BATCH_PROCESSING -> executeBatchProcessing(request.sql(), request.parameters());
                case STREAMING -> executeStreamingJob(request.sql(), request.parameters());
            };

            JobResponse completedJob = new JobResponse(
                    jobId,
                    request.jobName(),
                    JobResponse.JobStatus.COMPLETED,
                    jobs.get(jobId).startTime(),
                    LocalDateTime.now(),
                    result,
                    null
            );

            jobs.put(jobId, completedJob);
            logger.info("Job {} completed successfully", jobId);
            return completedJob;

        } catch (Exception e) {
            logger.error("Job {} execution failed", jobId, e);
            throw new RuntimeException("Job execution failed: " + e.getMessage(), e);
        }
    }

    private List<Map<String, Object>> executeSqlQuery(String sql) {
        Dataset<Row> dataset = sparkSession.sql(sql);
        return convertDatasetToList(dataset);
    }

    private List<Map<String, Object>> executeBatchProcessing(String sql, Map<String, String> parameters) {
        if (parameters != null) {
            parameters.forEach((key, value) -> sparkSession.conf().set(key, value));
        }

        Dataset<Row> dataset = sparkSession.sql(sql);
        return convertDatasetToList(dataset);
    }

    private List<Map<String, Object>> executeStreamingJob(String sql, Map<String, String> parameters) {
        throw new UnsupportedOperationException("Streaming jobs not yet implemented");
    }

    private List<Map<String, Object>> convertDatasetToList(Dataset<Row> dataset) {
        List<Map<String, Object>> result = new ArrayList<>();
        String[] columns = dataset.columns();

        List<Row> rows = dataset.collectAsList();
        for (Row row : rows) {
            Map<String, Object> record = new HashMap<>();
            for (int i = 0; i < columns.length; i++) {
                record.put(columns[i], row.get(i));
            }
            result.add(record);
        }

        return result;
    }
}