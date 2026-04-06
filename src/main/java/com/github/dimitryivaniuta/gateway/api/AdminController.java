package com.github.dimitryivaniuta.gateway.api;

import com.github.dimitryivaniuta.gateway.config.AppProperties;
import com.github.dimitryivaniuta.gateway.persistence.ReceivedEventEntity;
import com.github.dimitryivaniuta.gateway.persistence.ReceivedEventRepository;
import com.github.dimitryivaniuta.gateway.persistence.RejectedEventEntity;
import com.github.dimitryivaniuta.gateway.persistence.RejectedEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Minimal admin API for viewing received/rejected events and for controlled raw-envelope testing.
 *
 * <p>Endpoints under this controller are protected by the internal admin API key interceptor.
 */
@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
public class AdminController {

  private final ReceivedEventRepository receivedRepo;
  private final RejectedEventRepository rejectedRepo;
  private final KafkaTemplate<String, Object> kafkaTemplate;
  private final AppProperties appProperties;

  @PostMapping("/publish-raw")
  public AdminPublishResponse publishRaw(@RequestBody PublishRawEnvelopeRequest request) {
    kafkaTemplate.send(appProperties.kafka().topic(), request.envelope().aggregateId(), request.envelope());
    return new AdminPublishResponse(request.envelope().eventId(), appProperties.kafka().topic(), "raw-envelope");
  }

  @GetMapping("/received")
  public List<ReceivedEventEntity> received() {
    return receivedRepo.findAll(Sort.by(Sort.Direction.DESC, "receivedAt"));
  }

  @GetMapping("/rejected")
  public List<RejectedEventEntity> rejected() {
    return rejectedRepo.findAll(Sort.by(Sort.Direction.DESC, "rejectedAt"));
  }
}
