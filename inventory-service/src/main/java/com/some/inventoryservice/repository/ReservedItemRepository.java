package com.some.inventoryservice.repository;

import com.some.inventoryservice.model.entities.ReservedItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ReservedItemRepository extends JpaRepository<ReservedItemEntity, Long> {
    ReservedItemEntity findByOrderId(UUID orderId);

    List<ReservedItemEntity> findAllByOrderId(UUID uuid);

    boolean existsByOrderId(UUID orderId);
}
