package com.github.dimitryivaniuta.gateway.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dimitryivaniuta.gateway.config.AppProperties;
import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.persistence.ReceivedEventEntity;
import com.github.dimitryivaniuta.gateway.persistence.ReceivedEventRepository;
import com.github.dimitryivaniuta.gateway.replay.DedupStore;
import com.github.dimitryivaniuta.gateway.security.ReplayDetectedException;
import com.github.dimitryivaniuta.gateway.service.EventTimeValidator;
import com.github.dimitryivaniuta.gateway.service.EventVerifier;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * Kafka consumer that validates signature, freshness and replay protection before persisting the event.
 *
 * <p>Offset is acknowledged only after DB transaction commit.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecureEventsConsumer {

  private final AppProperties appProperties;
  private final EventVerifier verifier;
  private final EventTimeValidator eventTimeValidator;
  private final DedupStore dedupStore;
  private final ReceivedEventRepository receivedRepo;
  private final ObjectMapper objectMapper;
  private final MeterRegistry meterRegistry;

  private Counter processed;
  private Counter rejected;
  private Counter replay;

  @jakarta.annotation.PostConstruct
  void initMeters() {
    processed = meterRegistry.counter("secure_events_processed_total");
    rejected = meterRegistry.counter("secure_events_rejected_total");
    replay = meterRegistry.counter("secure_events_replay_total");
  }

  @KafkaListener(
      topics = "#{@appProperties.kafka().topic()}",
      containerFactory = "kafkaListenerContainerFactory"
  )
  @Transactional
  public void onMessage(SecureEventEnvelope envelope, Acknowledgment ack) {
    UUID eventId = envelope.eventId();

    verifier.verifyOrThrow(envelope);
    eventTimeValidator.validateOrThrow(envelope);

    boolean firstTime = dedupStore.markIfAbsent(eventId, appProperties.replayProtection().ttl());
    if (!firstTime) {
      replay.increment();
      rejected.increment();
      throw new ReplayDetectedException("Replay detected for eventId=" + eventId);
    }

    ReceivedEventEntity entity = new ReceivedEventEntity();
    entity.setEventId(envelope.eventId());
    entity.setEventType(envelope.eventType());
    entity.setKeyId(envelope.keyId());
    entity.setProducerId(envelope.producerId());
    entity.setAggregateId(envelope.aggregateId());
    entity.setOccurredAt(envelope.occurredAt());
    try {
      entity.setPayloadJson(objectMapper.writeValueAsString(envelope.payload()));
    } catch (Exception e) {
      throw new IllegalStateException("Failed to serialize payload for DB", e);
    }
    entity.setSignature(envelope.signature());
    entity.setReceivedAt(Instant.now());

    try {
      receivedRepo.saveAndFlush(entity);
    } catch (DataIntegrityViolationException ex) {
      replay.increment();
      rejected.increment();
      throw new ReplayDetectedException("Duplicate eventId detected by durable DB constraint: " + eventId);
    }

    processed.increment();
    ack.acknowledge();

    log.info("Processed secure event eventId={} type={} aggregateId={}",
        eventId, envelope.eventType(), envelope.aggregateId());
  }
}
