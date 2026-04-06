package com.github.dimitryivaniuta.gateway.kafka;

import com.github.dimitryivaniuta.gateway.config.AppProperties;
import com.github.dimitryivaniuta.gateway.persistence.RejectedEventRepository;
import com.github.dimitryivaniuta.gateway.security.EventTimeValidationException;
import com.github.dimitryivaniuta.gateway.security.ReplayDetectedException;
import com.github.dimitryivaniuta.gateway.security.SignatureValidationException;
import com.github.dimitryivaniuta.gateway.service.RejectionRecorder;
import java.util.function.BiFunction;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

/**
 * Kafka consumer error handling.
 *
 * <p>Invalid/replayed events are routed to the dead-letter topic and recorded in Postgres for audit.
 */
@Configuration
public class KafkaConsumerConfig {

  @Bean
  public RejectionRecorder rejectionRecorder(RejectedEventRepository repo) {
    return new RejectionRecorder(repo);
  }

  @Bean
  public DefaultErrorHandler kafkaErrorHandler(
      KafkaTemplate<String, Object> template,
      AppProperties appProperties,
      RejectionRecorder recorder
  ) {
    BiFunction<ConsumerRecord<?, ?>, Exception, TopicPartition> resolver =
        (rec, ex) -> new TopicPartition(appProperties.kafka().dltTopic(), rec.partition());

    DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(template, resolver);

    FixedBackOff backOff = new FixedBackOff(0L, 0L);
    DefaultErrorHandler handler = new DefaultErrorHandler((record, ex) -> {
      recorder.record(record, ex);
      recoverer.accept(record, ex);
    }, backOff);

    handler.addNotRetryableExceptions(
        SignatureValidationException.class,
        ReplayDetectedException.class,
        EventTimeValidationException.class,
        DeserializationException.class
    );

    return handler;
  }
}
