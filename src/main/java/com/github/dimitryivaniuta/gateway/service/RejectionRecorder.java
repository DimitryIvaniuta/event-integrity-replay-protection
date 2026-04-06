package com.github.dimitryivaniuta.gateway.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.persistence.RejectedEventEntity;
import com.github.dimitryivaniuta.gateway.persistence.RejectedEventRepository;
import com.github.dimitryivaniuta.gateway.security.EventTimeValidationException;
import com.github.dimitryivaniuta.gateway.security.ReplayDetectedException;
import com.github.dimitryivaniuta.gateway.security.SignatureValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.support.serializer.DeserializationException;

/** Records rejected events to Postgres for audit/forensics. */
@Slf4j
public class RejectionRecorder {

  private final RejectedEventRepository repo;
  private final ObjectMapper mapper = new ObjectMapper();

  public RejectionRecorder(RejectedEventRepository repo) {
    this.repo = repo;
  }

  public void record(ConsumerRecord<?, ?> record, Exception ex) {
    RejectedEventEntity e = new RejectedEventEntity();
    e.setReason(reason(ex));
    e.setDetails(ex.getMessage());

    Object value = record.value();
    if (value instanceof SecureEventEnvelope env) {
      e.setEventId(env.eventId());
      e.setEventType(env.eventType());
      e.setKeyId(env.keyId());
      e.setProducerId(env.producerId());
      e.setAggregateId(env.aggregateId());
      e.setOccurredAt(env.occurredAt());
      e.setSignature(env.signature());
      try {
        e.setPayloadJson(mapper.writeValueAsString(env.payload()));
      } catch (Exception ignore) {
        // ignored
      }
    }

    repo.save(e);

    log.warn("Rejected event topic={}, partition={}, offset={}, reason={}, message={}",
        record.topic(), record.partition(), record.offset(), e.getReason(), ex.getMessage());
  }

  private String reason(Exception ex) {
    if (ex instanceof SignatureValidationException) return "SIGNATURE_INVALID";
    if (ex instanceof ReplayDetectedException) return "REPLAY_DETECTED";
    if (ex instanceof EventTimeValidationException) return "EVENT_TIME_INVALID";
    if (ex instanceof DeserializationException) return "DESERIALIZATION_ERROR";
    return "PROCESSING_ERROR";
  }
}
