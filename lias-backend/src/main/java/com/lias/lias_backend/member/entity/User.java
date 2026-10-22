package com.lias.lias_backend.member.entity;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import lombok.*;

@org.hibernate.annotations.SQLDelete(sql = "UPDATE users SET archived = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("archived = false")
@Audited
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    public enum UserStatus {
        ACTIVE, FROZEN, DISABLED, PENDING
    }

    public enum UserRole {
        VISITOR, MEMBER, ASSOCIATE, DOCTORAL, DIRECTOR, VICE_DIRECTOR, TEAM_LEADER, ADMIN
    }
}