package com.JwtPulse.server.serviceImpl;

import com.JwtPulse.server.model.ServiceToken;
import com.JwtPulse.server.model.UserToken;
import com.JwtPulse.server.repository.ServiceTokenRepository;
import com.JwtPulse.server.repository.UserTokenRepository;
import com.JwtPulse.server.service.TokenService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final UserTokenRepository userTokenRepo;
    private final ServiceTokenRepository serviceTokenRepo;


    @Override
    public UserToken createUserToken(String email, Integer expiry, List<String> allowedIps) {
        UserToken token = UserToken.builder()
                .email(email)
                .createdAt(LocalDateTime.now())
                .expiry(expiry)
                .allowedIps(allowedIps)
                .build();
        return userTokenRepo.save(token);
    }

    @Override
    public void deleteUserToken(String email) {
        Optional<UserToken> userToken = userTokenRepo.findById(email);
        if (userToken.isEmpty()) return;
        userTokenRepo.delete(userToken.get());
    }

    @Override
    @Transactional
    public ServiceToken createServiceToken(UserToken userToken, String serviceURL, String credentials) {
        ServiceToken st = ServiceToken.builder()
                .serviceURL(serviceURL)
                .userToken(userToken)
                .tokenValue("")
                .credentials(credentials)
                .build();
        return serviceTokenRepo.save(st);
    }

    @Override
    public void deleteServiceToken(String serviceURL) {
        Optional<ServiceToken> serviceToken = serviceTokenRepo.findById(serviceURL);
        if (serviceToken.isEmpty()) return;
        serviceTokenRepo.delete(serviceToken.get());
    }

    @Override
    public boolean isTokenValid(String email, String ip) {
        String normalizedIp = ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip;
        return userTokenRepo.findById(email)
                .filter(t -> t.getCreatedAt().plusMinutes(t.getExpiry()).isAfter(LocalDateTime.now()))
                .filter(t -> t.getAllowedIps().isEmpty() || t.getAllowedIps().contains(normalizedIp))
                .isPresent();
    }

    @Override
    public void deleteAllServices(String email){
        if(email == null) return;
        Optional<UserToken> userToken = userTokenRepo.findById(email);
        if(userToken.isEmpty()) return;
        List<ServiceToken> serviceTokens = serviceTokenRepo.findAllByUserToken(userToken.get());
        for(ServiceToken s: serviceTokens){
            deleteServiceToken(s.getServiceURL());
        }
    }

    @Override
    public List<ServiceToken> getServiceTokens(UserToken userToken) {
        return serviceTokenRepo.findByUserToken(userToken);
    }

    @Override
    public Optional<UserToken> getUserTokenByEmail(String email) {
        return userTokenRepo.findById(email);
    }

    @Override
    public void refreshServiceToken(String serviceURL) {
        Optional<ServiceToken> opt = serviceTokenRepo.findById(serviceURL);
        if (opt.isEmpty()) return;

        ServiceToken st = opt.get();
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> req = new HttpEntity<>(st.getCredentials(), headers);
            ResponseEntity<String> res = new RestTemplate().postForEntity(st.getServiceURL(), req, String.class);
            st.setTokenValue(res.getBody());
            serviceTokenRepo.save(st);
        } catch (Exception e) {
        }
    }

}