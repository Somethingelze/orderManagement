package com.some.inventoryservice.repository;

import com.some.inventoryservice.model.redisHash.redisHash.RedisReservation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ReservationProductsRepository extends CrudRepository<RedisReservation, String> {

    RedisReservation findByOrderId(String orderId);
}
