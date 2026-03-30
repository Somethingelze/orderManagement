package com.some.inventoryservice.repository;

import com.some.inventoryservice.model.entities.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findById(UUID id);
    void deleteById(UUID id);
}
