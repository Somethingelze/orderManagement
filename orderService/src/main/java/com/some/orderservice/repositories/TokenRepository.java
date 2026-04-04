package com.some.orderservice.repositories;

import com.some.orderservice.model.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TokenRepository extends JpaRepository<TokenEntity, Long> {

    List<TokenEntity> findAllByUserIdAndLoggedOutFalse(UUID user_id);

    Optional<TokenEntity> findByAccessToken(String accessToken);

    Optional<TokenEntity> findByRefreshToken(String refreshToken);
}
