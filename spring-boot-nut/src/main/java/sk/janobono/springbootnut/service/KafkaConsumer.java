package sk.janobono.springbootnut.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "spring-boot-nut", groupId = "group_id")
    public void consume(String message) throws IOException {
        LOGGER.info("consume({})", message);
    }
}
