package com.imperium.ims.entity;

import com.imperium.ims.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "login_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "login_time", nullable = false)
    @Builder.Default
    private LocalDateTime loginTime = LocalDateTime.now();

    @Column(nullable = false, length = 20)
    private String status; // e.g. "SUCCESS", "FAILED"
}
