package com.JwtPulse.server.repository;

import com.JwtPulse.server.model.ServiceToken;
import com.JwtPulse.server.model.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceTokenRepository extends JpaRepository<ServiceToken, String> {
    List<ServiceToken> findByUserToken(UserToken userToken);
    List<ServiceToken> findAllByUserToken(UserToken userToken);
}