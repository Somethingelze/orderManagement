package com.some.orderservice.model.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column
    private String userEmail;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItemEntity> orderItems;

    @Column(nullable = false)
    BigDecimal totalPrice;

    @Column(nullable = false)
    @CreationTimestamp
    LocalDateTime createdAt;

}
