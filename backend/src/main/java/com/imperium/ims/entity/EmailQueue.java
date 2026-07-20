package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "email_queue")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailQueue extends BaseEntity {

    @Column(name = "recipient_email", nullable = false)
    private String recipientEmail;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "body", nullable = false, columnDefinition = "TEXT")
    private String body;

    @Column(name = "status", nullable = false)
    @Builder.Default
    private String status = "PENDING";
}
