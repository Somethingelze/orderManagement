package com.some.orderservice.model.redisHash;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "product_reservation", timeToLive = 3600)
public class RedisReservation {

    @Id
    private String orderId;
    private Map<String, Long> reservedProducts;
}