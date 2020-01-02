package sk.janobono.quarkusnut.controller;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.Container;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class UserControllerTest {

    public static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:9-alpine");

    public static KafkaContainer kafka = new KafkaContainer();

    @BeforeAll
    public static void startContainers() {
        postgres.start();
        System.setProperty("NUT_DB_URL", postgres.getJdbcUrl());
        System.setProperty("NUT_DB_USER", postgres.getUsername());
        System.setProperty("NUT_DB_PASSWORD", postgres.getPassword());

        kafka.start();
        createTopic("quarkus-boot-nut");
        System.setProperty("NUT_KAFKA_BOOTSTRAP_SERVERS", kafka.getBootstrapServers());
    }

    private static void createTopic(String topicName) {
        String createTopic =
                String.format(
                        "/usr/bin/kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic %s",
                        topicName);
        try {
            final Container.ExecResult execResult = kafka.execInContainer("/bin/sh", "-c", createTopic);
            if (execResult.getExitCode() != 0) throw new RuntimeException("Kafka topic not created!");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @AfterAll
    public static void stopContainers() {
        postgres.stop();
        kafka.stop();
    }

    @Test
    public void testHelloEndpoint() {
        given()
                .when().get("/user")
                .then()
                .statusCode(200)
                .body(is("hello"));
    }

}
