package com.JwtPulse.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "service_token")
public class ServiceToken {
    @Id
    private String serviceURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "token_id", nullable = false)
    private UserToken userToken;

    private String tokenValue;

    @Lob
    private String credentials;
}
