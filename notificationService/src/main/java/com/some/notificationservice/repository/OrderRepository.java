package com.some.notificationservice.repository;

import com.some.notificationservice.model.entity.OrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<OrderEntity, Long> {

    Page<OrderEntity> findAllOrdersByUserId(UUID userId, Pageable pageable);
}
