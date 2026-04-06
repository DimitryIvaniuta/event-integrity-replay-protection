package com.github.dimitryivaniuta.gateway.service;

import com.github.dimitryivaniuta.gateway.config.AppProperties;
import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/** Publishes signed secure events to Kafka. */
@Slf4j
@Service
@RequiredArgsConstructor
public class SecureEventPublisher {

  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final AppProperties appProperties;

  public void publish(SecureEventEnvelope envelope) {
    String topic = appProperties.kafka().topic();
    kafkaTemplate.send(topic, envelope.aggregateId(), envelope);
    log.info("Published secure event eventId={} type={} aggregateId={} topic={}",
        envelope.eventId(), envelope.eventType(), envelope.aggregateId(), topic);
  }
}
