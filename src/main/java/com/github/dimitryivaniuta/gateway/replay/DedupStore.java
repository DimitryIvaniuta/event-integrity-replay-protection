package com.github.dimitryivaniuta.gateway.replay;

import java.time.Duration;
import java.util.UUID;

/** Stores eventIds to prevent replays. */
public interface DedupStore {

  /**
   * Marks the eventId as seen for the given TTL.
   *
   * @return true if the eventId was absent and is now stored; false if it already existed.
   */
  boolean markIfAbsent(UUID eventId, Duration ttl);
}
