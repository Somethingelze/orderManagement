package com.some.notificationservice.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "kafka.order-events")
public record OrderConsumerProperties(
    @NotBlank String topic,
    @NotBlank String groupId
) {}