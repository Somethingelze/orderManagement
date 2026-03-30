package com.some.orderservice.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "order_id")
    private UUID orderId;

    @Column(name = "product_id")
    private UUID productId;

    @Column
    private Long quantity;

    @Column
    private BigDecimal price;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column
    private BigDecimal sale;

    private UUID userId;
}
