package com.github.dimitryivaniuta.gateway.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Stores successfully validated events (audit).
 *
 * <p>eventId has a unique constraint; it protects against duplicates even if Redis is flushed.
 */
@Entity
@Table(name = "received_events")
@Getter
@Setter
@NoArgsConstructor
public class ReceivedEventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id", nullable = false, unique = true)
  private UUID eventId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "key_id", nullable = false)
  private String keyId;

  @Column(name = "producer_id", nullable = false)
  private String producerId;

  @Column(name = "aggregate_id", nullable = false)
  private String aggregateId;

  @Column(name = "occurred_at", nullable = false)
  private Instant occurredAt;

  @Column(name = "payload", nullable = false, columnDefinition = "jsonb")
  private String payloadJson;

  @Column(name = "signature", nullable = false)
  private String signature;

  @Column(name = "received_at", nullable = false)
  private Instant receivedAt = Instant.now();
}
