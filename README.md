# Spark as a Service

A Spring Boot application that provides RESTful endpoints for submitting and managing Apache Spark jobs.

## Tech Stack

- **JDK 21**
- **Spring Boot 3.5.6** (Latest)
- **Spring Boot Integration**
- **Apache Spark 3.5.6**
- **Spring WebFlux** (Reactive)
- **Docker**

## Features

- RESTful API for Spark job submission
- Asynchronous job execution
- Job status tracking
- Spark session management
- Docker containerization
- Health monitoring with Spring Actuator
- Interactive API documentation with Swagger UI
- OpenAPI 3.0 specification

## Local Development

### Prerequisites

- Docker and Docker Compose
- WSL (Windows Subsystem for Linux)

### Running the Application

1. Clone the repository
2. Build and run with Docker Compose:

```bash
docker-compose up --build
```

3. The application will be available at:
   - **Spring Boot API**: http://localhost:8080
   - **Spark UI**: http://localhost:4040
   - **Swagger UI**: http://localhost:8080/swagger-ui.html
   - **API Documentation**: http://localhost:8080/v3/api-docs

### API Endpoints

#### Submit a Job
```bash
POST /api/spark/jobs
Content-Type: application/json

{
  "jobName": "sample-query",
  "jobType": "SQL_QUERY",
  "sql": "SELECT 1 as test_column",
  "parameters": {}
}
```

#### Get Job Status
```bash
GET /api/spark/jobs/{jobId}
```

#### Get All Jobs
```bash
GET /api/spark/jobs
```

#### Get Spark Info
```bash
GET /api/spark/info
```

#### Health Check
```bash
GET /actuator/health
```

#### API Documentation
```bash
GET /swagger-ui.html        # Interactive Swagger UI
GET /v3/api-docs            # OpenAPI JSON specification
GET /v3/api-docs.yaml       # OpenAPI YAML specification
```

### Example Usage

1. Submit a simple SQL query:
```bash
curl -X POST http://localhost:8080/api/spark/jobs \
  -H "Content-Type: application/json" \
  -d '{
    "jobName": "test-query",
    "jobType": "SQL_QUERY",
    "sql": "SELECT 1 as number, \"Hello Spark\" as message"
  }'
```

2. Check job status:
```bash
curl http://localhost:8080/api/spark/jobs/{jobId}
```

3. Interactive API testing:
   - Open http://localhost:8080/swagger-ui.html in your browser
   - Use the interactive interface to test all endpoints
   - View request/response schemas and examples

## Production Architecture Considerations

### Deployment Options

#### Option 1: Standalone Service (Recommended)
- Deploy the service on dedicated servers/containers
- Use `spark.master=spark://spark-master:7077` to connect to external Spark cluster
- Benefits: Better resource isolation, independent scaling, centralized job management

#### Option 2: Co-located with Spark Master
- Deploy as a sidecar container in Spark master pod
- Acts as a wrapper around spark-submit
- Benefits: Lower network latency, simpler networking

### Production Configuration

For production, update `application.yml`:

```yaml
spark:
  master: spark://spark-master:7077  # External cluster
  config:
    spark.sql.warehouse.dir: hdfs://namenode:9000/user/spark/warehouse
    spark.eventLog.enabled: true
    spark.eventLog.dir: hdfs://namenode:9000/user/spark/logs
    spark.history.fs.logDirectory: hdfs://namenode:9000/user/spark/logs
```

### Scaling Considerations

1. **Horizontal Scaling**: Multiple instances behind a load balancer
2. **Job Queue**: Add Redis/RabbitMQ for job queuing
3. **Persistence**: Store job metadata in database (PostgreSQL/MongoDB)
4. **Monitoring**: Integrate with Prometheus/Grafana
5. **Security**: Add authentication and authorization

## Development Notes

- Logs are available in `./logs` directory
- Data files can be placed in `./data` directory
- The application uses WebFlux for reactive programming
- Spark UI is accessible at port 4040 when jobs are running