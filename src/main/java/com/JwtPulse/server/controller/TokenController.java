package com.JwtPulse.server.controller;

import com.JwtPulse.server.model.ServiceToken;
import com.JwtPulse.server.model.UserToken;
import com.JwtPulse.server.service.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/")
@RequiredArgsConstructor
public class TokenController {

    private final TokenService tokenService;

    @GetMapping("/token")
    public ResponseEntity<String> generateToken(
            @RequestParam String email,
            @RequestParam int expiryMinutes,
            @RequestParam List<String> allowedIps) {

        UserToken token = tokenService.createUserToken(email, expiryMinutes, allowedIps);
        if(token != null) return ResponseEntity.ok(email);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid input");
    }

    @PostMapping("/service")
    public ResponseEntity<String> addService(
            @RequestHeader("Token") String email,
            @RequestParam("url") String serviceURL,
            @RequestBody String credentialsJson,
            HttpServletRequest request) {

        Optional<UserToken> userTokenOpt = tokenService.getUserTokenByEmail(email);
        if (userTokenOpt.isEmpty() || !tokenService.isTokenValid(email, request.getRemoteAddr())) {
            tokenService.deleteAllServices(email);
            tokenService.deleteUserToken(email);
            return ResponseEntity.status(403).body("Invalid or expired token.");
        }
        ServiceToken serviceToken = tokenService.createServiceToken(userTokenOpt.get(), serviceURL, credentialsJson);
        tokenService.refreshServiceToken(serviceURL);
        return ResponseEntity.ok(serviceToken.getServiceURL());
    }

    @GetMapping("/services")
    public ResponseEntity<?> listAllServices(
            @RequestHeader("Token") String email,
            HttpServletRequest request) {

        Optional<UserToken> userTokenOpt = tokenService.getUserTokenByEmail(email);
        if (userTokenOpt.isEmpty() || !tokenService.isTokenValid(email, request.getRemoteAddr())) {
            tokenService.deleteAllServices(email);
            tokenService.deleteUserToken(email);
            return ResponseEntity.status(403).body("Invalid or expired token.");
        }
        List<ServiceToken> services = tokenService.getServiceTokens(userTokenOpt.get());
        return ResponseEntity.ok(services);
    }


    @GetMapping("/service")
    public ResponseEntity<?> getServices(
            @RequestHeader("Token") String email,
            @RequestParam("url") String url,
            HttpServletRequest request) {

        Optional<UserToken> userTokenOpt = tokenService.getUserTokenByEmail(email);
        if (userTokenOpt.isEmpty() || !tokenService.isTokenValid(email, request.getRemoteAddr())) {
            tokenService.deleteAllServices(email);
            tokenService.deleteUserToken(email);
            return ResponseEntity.status(403).body("Invalid or expired token.");
        }
        List<ServiceToken> services = tokenService.getServiceTokens(userTokenOpt.get());
        for(ServiceToken serviceToken : services){
            if(serviceToken.getServiceURL().equals(url))
                return ResponseEntity.ok(serviceToken.getTokenValue());
        }
        return ResponseEntity.status(404).body("Service token not found");
    }

    @PostMapping("/service/refresh")
    public ResponseEntity<?> refreshService(
            @RequestHeader("Token") String email,
            @RequestParam("url") String url,
            HttpServletRequest request) {

        Optional<UserToken> userTokenOpt = tokenService.getUserTokenByEmail(email);
        if (userTokenOpt.isEmpty() || !tokenService.isTokenValid(email, request.getRemoteAddr())) {
            tokenService.deleteAllServices(email);
            tokenService.deleteUserToken(email);
            return ResponseEntity.status(403).body("Invalid or expired token.");
        }
        tokenService.refreshServiceToken(url);
        return ResponseEntity.ok("Service refreshed");
    }

    @DeleteMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Token") String email, HttpServletRequest request) {
        Optional<UserToken> userTokenOpt = tokenService.getUserTokenByEmail(email);
        if (userTokenOpt.isEmpty() || !tokenService.isTokenValid(email, request.getRemoteAddr())) {
            tokenService.deleteAllServices(email);
            tokenService.deleteUserToken(email);
            return ResponseEntity.status(403).body("Invalid or expired token.");
        }
        tokenService.deleteAllServices(email);
        tokenService.deleteUserToken(email);
        return ResponseEntity.ok("Token invalidated and deleted.");
    }
}
