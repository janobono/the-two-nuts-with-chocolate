package sk.janobono.quarkusnut.service;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Singleton;
import java.util.Properties;

@Singleton
public class KafkaProducerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducerService.class);

    private KafkaProducer<String, String> kafkaProducer;

    @ConfigProperty(name = "kafka.bootstrap-servers")
    String bootstrapServers;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        kafkaProducer = new KafkaProducer<>(props);
        kafkaProducer.flush();
    }

    @PreDestroy
    public void close() {
        kafkaProducer.close();
    }

    public void sendMessage(String message) {
        LOGGER.info("sendMessage({})", message);
        ProducerRecord<String, String> record = new ProducerRecord<>("quarkus-nut", message);
        kafkaProducer.send(record);
    }
}
