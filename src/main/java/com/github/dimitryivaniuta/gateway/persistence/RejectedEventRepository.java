package com.github.dimitryivaniuta.gateway.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Repository for rejected events. */
public interface RejectedEventRepository extends JpaRepository<RejectedEventEntity, Long> {}
