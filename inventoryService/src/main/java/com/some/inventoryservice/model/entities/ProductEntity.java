package com.some.inventoryservice.model.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@RequiredArgsConstructor
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    String id;

    @Column
    String name;

    @Column
    Long quantity;

    @Column
    BigDecimal price;

    @Column
    BigDecimal sale;
}

