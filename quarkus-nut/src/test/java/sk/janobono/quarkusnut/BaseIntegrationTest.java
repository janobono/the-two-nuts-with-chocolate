package sk.janobono.quarkusnut;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.flywaydb.core.Flyway;
import org.junit.Assert;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.shaded.org.apache.commons.lang.StringUtils;
import sk.janobono.quarkusnut.so.Page;
import sk.janobono.quarkusnut.so.Pageable;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class BaseIntegrationTest {

    protected static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:9-alpine");

    protected static KafkaContainer kafka = new KafkaContainer();

    @BeforeAll
    public static void startContainers() {
        postgres.start();
        System.setProperty("NUT_DB_URL", postgres.getJdbcUrl());
        System.setProperty("NUT_DB_USER", postgres.getUsername());
        System.setProperty("NUT_DB_PASSWORD", postgres.getPassword());

        kafka.start();
        createTopic("spring-boot-nut");
        System.setProperty("NUT_KAFKA_BOOTSTRAP_SERVERS", kafka.getBootstrapServers());
    }

    private static void createTopic(String topicName) {
        String createTopic =
                String.format(
                        "/usr/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic %s",
                        topicName);
        try {
            final Container.ExecResult execResult = kafka.execInContainer("/bin/sh", "-c", createTopic);
            if (execResult.getExitCode() != 0) Assert.fail();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void stopContainers() {
        postgres.stop();
        kafka.stop();
    }

    protected EnhancedRandom enhancedRandom = TestEnhancedRandomBuilder.build();

    @Inject
    Flyway flyway;

    protected static final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        flyway.clean();
        flyway.migrate();
    }

    protected String paginatedUri(String uri, Integer page, Integer pageSize, String sortCol, Pageable.SortDirection direction) {
        StringBuilder builder = new StringBuilder();
        builder.append(uri);
        builder.append("?page=").append(page);
        builder.append("&size=").append(pageSize);
        if (!StringUtils.isEmpty(sortCol)) {
            builder.append("&sort=").append(sortCol).append(",").append(direction.name());
        }
        return builder.toString();
    }

    protected <T> Page<T> mapPagedResponse(String json, Class<T> paramClazz) throws IOException {
        System.out.println(json);
        JsonNode parent = objectMapper.readTree(json);
        Page<T> result = new Page<>();
        result.setContent(getListFromNode(parent.get("content"), paramClazz));
        result.setPageable(new Pageable());
        result.getPageable().setPageSize(parent.get("pageable").get("pageSize").asInt());
        result.getPageable().setPageNumber(parent.get("pageable").get("pageNumber").asInt());
        result.getPageable().setSortField(parent.get("pageable").get("sortField").asText());
        String sortDirection = parent.get("pageable").get("sortDirection").asText();
        if (sortDirection != null) {
            result.getPageable().setSortDirection(Pageable.SortDirection.valueOf(sortDirection));
        }
        return result;
    }

    private <T> List<T> getListFromNode(JsonNode node, Class<T> clazz) throws IOException {
        List<T> content = new ArrayList<>();
        for (JsonNode val : node) {
            content.add(objectMapper.readValue(val.traverse(), clazz));
        }
        return content;
    }

    protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}
