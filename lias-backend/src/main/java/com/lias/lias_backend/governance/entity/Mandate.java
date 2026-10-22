package com.lias.lias_backend.governance.entity;

import com.lias.lias_backend.member.entity.BaseEntity;
import com.lias.lias_backend.member.entity.Member;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import lombok.*;

import java.time.LocalDate;

@org.hibernate.annotations.SQLDelete(sql = "UPDATE mandates SET archived = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("archived = false")
@Audited
@Entity
@Table(name = "mandates")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Mandate extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MandateRole role;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    private String team;

    public boolean isActive() {
        return !startDate.isAfter(LocalDate.now()) && (endDate == null || !endDate.isBefore(LocalDate.now()));
    }

    public enum MandateRole {
        DIRECTOR, VICE_DIRECTOR, TEAM_LEADER, TEAM_MEMBER
    }
}