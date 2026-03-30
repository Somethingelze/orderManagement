package com.some.orderservice.configuration;

import com.some.grpc.inventory.InventoryServiceGrpc;
import io.grpc.Channel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

    @Bean
    public InventoryServiceGrpc.InventoryServiceBlockingStub inventoryServiceStub(
            GrpcChannelFactory channelFactory) {
        Channel channel = channelFactory.createChannel("inventory-service");
        return InventoryServiceGrpc.newBlockingStub(channel);
    }
}