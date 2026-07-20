package com.imperium.ims.entity;

import com.imperium.ims.entity.BaseEntity;
import com.imperium.ims.common.enums.Status;
import com.imperium.ims.entity.User;
import jakarta.persistence.*;
import lombok.*;

/**
 * Represents a review/rating for an application.
 */
@Entity
@Table(name = "application_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationReview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "reviewer_id", nullable = false)
    private User reviewer;

    @Column(name = "rating", nullable = false)
    private Integer rating;

    @Column(name = "comments", length = 1000)
    private String comments;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private Status status = Status.ACTIVE;
}
