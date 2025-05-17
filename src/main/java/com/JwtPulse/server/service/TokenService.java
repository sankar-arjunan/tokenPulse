package com.JwtPulse.server.service;

import com.JwtPulse.server.model.ServiceToken;
import com.JwtPulse.server.model.UserToken;

import java.util.List;
import java.util.Optional;

public interface TokenService {
    UserToken createUserToken(String email, Integer expiryMinutes, List<String> allowedIps);
    void deleteUserToken(String email);
    ServiceToken createServiceToken(UserToken userToken, String serviceURL, String credentials);
    void deleteServiceToken(String serviceURL);
    boolean isTokenValid(String email, String ip);

    void deleteAllServices(String email);

    List<ServiceToken> getServiceTokens(UserToken userToken);
    Optional<UserToken> getUserTokenByEmail(String email);
    void refreshServiceToken(String serviceURL);
}
