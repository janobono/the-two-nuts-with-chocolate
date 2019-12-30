package sk.janobono.springbootnut;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.benas.randombeans.api.EnhancedRandom;
import org.flywaydb.core.Flyway;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest()
@AutoConfigureMockMvc
@DirtiesContext
public abstract class BaseIntegrationTest {

    public static PostgreSQLContainer postgres = new PostgreSQLContainer<>();

    public static RabbitMQContainer rabbitmq = new RabbitMQContainer();

    @BeforeClass
    public static void startContainers() {
        postgres.start();
        System.setProperty("NUT_DB_URL", "jdbc:postgresql://localhost:" + postgres.getMappedPort(5432) + "/" + postgres.getDatabaseName());
        System.setProperty("NUT_DB_USER", postgres.getUsername());
        System.setProperty("NUT_DB_PASSWORD", postgres.getPassword());

        rabbitmq.start();
        System.setProperty("NUT_RABBIT_MQ_HOST", "localhost");
        System.setProperty("NUT_RABBIT_MQ_PORT", "" + rabbitmq.getMappedPort(5672));
        System.setProperty("NUT_RABBIT_MQ_USERNAME", rabbitmq.getAdminUsername());
        System.setProperty("NUT_RABBIT_MQ_PASSWORD", rabbitmq.getAdminPassword());
    }

    @AfterClass
    public static void stopContainers() {
        postgres.stop();
        rabbitmq.stop();
    }

    public EnhancedRandom enhancedRandom = TestEnhancedRandomBuilder.build();

    @Autowired
    public MockMvc mvc;

    @Autowired
    public Flyway flyway;

    @Autowired
    public WebApplicationContext webApplicationContext;

    @Autowired
    public ObjectMapper objectMapper;

    @Before
    public void setUp() {
        flyway.clean();
        flyway.migrate();
        mvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    protected String paginatedUri(String uri, Integer page, Integer pageSize, String sortCol, Sort.Direction direction) {
        StringBuilder builder = new StringBuilder();
        builder.append(uri);
        builder.append("?page=").append(page);
        builder.append("&size=").append(pageSize);
        if (!StringUtils.isEmpty(sortCol)) {
            builder.append("&sort=").append(sortCol).append(",").append(direction.name());
        }
        return builder.toString();
    }

    protected String sortedUri(String uri, String sortCol, Sort.Direction direction) {
        StringBuilder builder = new StringBuilder();
        builder.append(uri);
        builder.append("?sort=").append(sortCol).append(",").append(direction.name());
        return builder.toString();
    }

    protected String mapToJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }

    protected <T> T mapFromJson(String json, Class<T> clazz) throws IOException {
        return objectMapper.readValue(json, clazz);
    }

    protected <T> Page<T> mapPagedResponse(String json, Class<T> paramClazz) throws IOException {
        JsonNode parent = objectMapper.readTree(json);
        return new PageImpl<>(
                getListFromNode(parent.get("content"), paramClazz),
                PageRequest.of(
                        parent.get("pageable").get("pageNumber").asInt(),
                        parent.get("pageable").get("pageSize").asInt()),
                parent.get("totalElements").asLong());
    }

    private <T> List<T> getListFromNode(JsonNode node, Class<T> clazz) throws IOException {
        List<T> content = new ArrayList<>();
        for (JsonNode val : node) {
            content.add(objectMapper.readValue(val.traverse(), clazz));
        }
        return content;
    }
}