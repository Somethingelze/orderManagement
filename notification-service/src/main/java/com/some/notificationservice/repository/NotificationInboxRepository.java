package com.some.notificationservice.repository;

import com.some.notificationservice.model.entity.NotificationInboxEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface NotificationInboxRepository extends JpaRepository<NotificationInboxEntity, UUID> {
}
