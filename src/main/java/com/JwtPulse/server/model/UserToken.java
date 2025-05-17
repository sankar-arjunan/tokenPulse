package com.JwtPulse.server.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "user_token")
public class UserToken {
    @Id
    private String email;

    private LocalDateTime createdAt;

    private Integer expiry;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> allowedIps;

}
