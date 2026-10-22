package com.lias.lias_backend.notification.entity;

import com.lias.lias_backend.member.entity.BaseEntity;
import com.lias.lias_backend.member.entity.Member;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import lombok.*;

@org.hibernate.annotations.SQLDelete(sql = "UPDATE notifications SET archived = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("archived = false")
@Audited
@Entity
@Table(name = "notifications")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notification extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private boolean read;

    public enum NotificationType {
        ACCOUNT_APPROVED, ACCOUNT_REJECTED, NEW_EVENT,
        NEW_DOCUMENT, NEW_MEETING, NEW_MEMBER_PENDING, REQUEST_DECISION
    }
}
