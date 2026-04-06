package com.github.dimitryivaniuta.gateway.kafka;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

/** Kafka consumer factory configuration. */
@Configuration
public class KafkaConsumerFactoryConfig {

  @Bean
  public ConsumerFactory<String, Object> consumerFactory(org.springframework.boot.autoconfigure.kafka.KafkaProperties props) {
    Map<String, Object> cfg = new HashMap<>(props.buildConsumerProperties());
    cfg.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    cfg.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
    cfg.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
    cfg.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
    cfg.put(JsonDeserializer.TRUSTED_PACKAGES, "com.github.dimitryivaniuta.gateway.*");
    cfg.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope");
    cfg.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
    return new DefaultKafkaConsumerFactory<>(cfg);
  }
}
