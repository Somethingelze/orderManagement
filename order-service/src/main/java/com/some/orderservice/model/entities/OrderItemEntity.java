package com.some.orderservice.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "order_items")
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "order")
public class OrderItemEntity {

    @Id
    private UUID id;

    @Column
    private UUID productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderEntity order;

    @Column
    private String productName;

    @Column
    private BigDecimal price;

    @Column
    private BigDecimal sale;

    @Column
    private BigDecimal totalPrice;
}