package com.github.dimitryivaniuta.gateway.api;

import com.github.dimitryivaniuta.gateway.domain.SecureEventEnvelope;
import com.github.dimitryivaniuta.gateway.service.EventSigner;
import com.github.dimitryivaniuta.gateway.service.SecureEventPublisher;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/** REST API to publish signed events (producer side). */
@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventsController {

  private final EventSigner signer;
  private final SecureEventPublisher publisher;

  @PostMapping("/publish")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public PublishEventResponse publish(@Valid @RequestBody PublishEventRequest req) {
    SecureEventEnvelope env = signer.signNew(req.eventType(), req.producerId(), req.aggregateId(), req.payload());
    publisher.publish(env);
    return new PublishEventResponse(env.eventId(), "secure-events");
  }
}
