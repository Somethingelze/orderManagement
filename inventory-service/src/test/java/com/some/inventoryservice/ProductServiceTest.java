package com.some.inventoryservice;

import com.some.grpc.inventory.ProductRequestDto;
import com.some.grpc.inventory.ProductResponseDto;
import com.some.grpc.inventory.OrderItemDto;
import com.some.inventoryservice.exceptions.ProductNotFoundException;
import com.some.inventoryservice.model.entities.ProductEntity;
import com.some.inventoryservice.repository.ProductRepository;
import com.some.inventoryservice.services.impl.ProductServiceImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Functional Tests")
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Nested
    @DisplayName("Inventory Collection Logic")
    class CollectItemsTests {

        @Test
        @DisplayName("Should deduct stock when requested quantity is exactly available")
        void collectItems_exactQuantity_success() {
            String productId = UUID.randomUUID().toString();
            ProductRequestDto request = ProductRequestDto.newBuilder()
                    .putOrderItems(productId, 10L)
                    .build();

            ProductEntity product = ProductEntity.builder()
                    .id(productId)
                    .name("Test Product")
                    .quantity(10L)
                    .price(new BigDecimal("50.00"))
                    .sale(new BigDecimal("5.00"))
                    .build();

            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            ProductResponseDto response = productService.collectItems(request);

            OrderItemDto item = response.getItems(0);
            assertThat(item.getIsAvailable()).isTrue();
            assertThat(item.getProductName()).isEqualTo("Test Product");
            assertThat(item.getTotalPrice()).isEqualTo(45000L);
            assertThat(product.getQuantity()).isZero();
        }

        @Test
        @DisplayName("Should flag as unavailable and keep stock when inventory is insufficient")
        void collectItems_insufficientStock_fail() {
            String productId = UUID.randomUUID().toString();
            ProductRequestDto request = ProductRequestDto.newBuilder()
                    .putOrderItems(productId, 11L)
                    .build();

            ProductEntity product = ProductEntity.builder()
                    .id(productId)
                    .name("Test Product")
                    .quantity(10L)
                    .price(BigDecimal.TEN)
                    .sale(BigDecimal.ZERO)
                    .build();

            given(productRepository.findById(productId)).willReturn(Optional.of(product));

            ProductResponseDto response = productService.collectItems(request);

            assertThat(response.getItems(0).getIsAvailable()).isFalse();
            assertThat(response.getItems(0).getProductName()).isEqualTo("Test Product");
            assertThat(product.getQuantity()).isEqualTo(10L);
        }
    }

    @Nested
    @DisplayName("Product Lifecycle Operations")
    class LifecycleTests {

        @Test
        void updateProduct_nonExistent_throwsException() {
            String id = UUID.randomUUID().toString();
            ProductEntity updateData = new ProductEntity();

            given(productRepository.findById(id)).willReturn(Optional.empty());

            assertThatThrownBy(() -> productService.updateProduct(id, updateData))
                    .isInstanceOf(ProductNotFoundException.class);

            verify(productRepository, never()).save(any());
        }

        @Test
        void getProductById_found_returnsEntity() {
            String id = UUID.randomUUID().toString();
            ProductEntity expected = new ProductEntity();
            expected.setId(id);

            given(productRepository.findById(id)).willReturn(Optional.of(expected));

            ProductEntity result = productService.getProductById(id);

            assertThat(result).isEqualTo(expected);
        }
    }

    @Test
    void convertToPennies_validInput_returnsLong() {
        assertThat(productService.convertToPennies(new BigDecimal("123.45"))).isEqualTo(12345L);
        assertThat(productService.convertToPennies(new BigDecimal("0.00"))).isZero();
    }
}