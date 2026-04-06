package com.github.dimitryivaniuta.gateway.persistence;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for received events. */
public interface ReceivedEventRepository extends JpaRepository<ReceivedEventEntity, Long> {
  Optional<ReceivedEventEntity> findByEventId(UUID eventId);
}
