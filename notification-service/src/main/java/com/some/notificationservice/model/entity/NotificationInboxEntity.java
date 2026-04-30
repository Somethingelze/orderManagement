package com.some.notificationservice.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "notification_inbox")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class NotificationInboxEntity {

    @Id
    private UUID messageId;

    private LocalDateTime processedAt;
}