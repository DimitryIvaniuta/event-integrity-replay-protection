package com.github.dimitryivaniuta.gateway.persistence;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Stores rejected events for audit and incident response. */
@Entity
@Table(name = "rejected_events")
@Getter
@Setter
@NoArgsConstructor
public class RejectedEventEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "event_id")
  private UUID eventId;

  @Column(name = "event_type")
  private String eventType;

  @Column(name = "key_id")
  private String keyId;

  @Column(name = "producer_id")
  private String producerId;

  @Column(name = "aggregate_id")
  private String aggregateId;

  @Column(name = "occurred_at")
  private Instant occurredAt;

  @Column(name = "payload", columnDefinition = "jsonb")
  private String payloadJson;

  @Column(name = "signature")
  private String signature;

  @Column(name = "reason", nullable = false)
  private String reason;

  @Column(name = "details")
  private String details;

  @Column(name = "rejected_at", nullable = false)
  private Instant rejectedAt = Instant.now();
}
