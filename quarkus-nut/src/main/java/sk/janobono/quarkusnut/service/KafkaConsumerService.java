package sk.janobono.quarkusnut.service;

import io.quarkus.scheduler.Scheduled;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

@Singleton
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    private KafkaConsumer<String, String> kafkaConsumer;

    @ConfigProperty(name = "kafka.bootstrap-servers")
    String bootstrapServers;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "group-id");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(Collections.singletonList("quarkus-nut"));
    }

    @PreDestroy
    public void close() {
        kafkaConsumer.unsubscribe();
        kafkaConsumer.close();
    }

    @Scheduled(every = "10s")
    void consume() {
        ConsumerRecords<String, String> records = kafkaConsumer.poll(Duration.ofMillis(500));
        records.forEach(r -> {
            LOGGER.info("consume({})", r.value());
        });
    }
}
