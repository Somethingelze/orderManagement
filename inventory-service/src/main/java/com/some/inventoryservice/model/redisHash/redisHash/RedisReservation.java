package com.some.inventoryservice.model.redisHash.redisHash;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash(value = "product_reservation")
public class RedisReservation {

    @Id
    private String orderId;
    private Map<String, Long> reservedProducts;
}