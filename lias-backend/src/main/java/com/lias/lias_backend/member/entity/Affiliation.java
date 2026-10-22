package com.lias.lias_backend.member.entity;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import lombok.*;
import java.time.LocalDate;

@org.hibernate.annotations.SQLDelete(sql = "UPDATE affiliations SET archived = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("archived = false")
@Audited
@Entity
@Table(name = "affiliations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Affiliation extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    private String laboratory;
    private String team;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    public boolean isActive() {
        return !startDate.isAfter(LocalDate.now()) && (endDate == null || !endDate.isBefore(LocalDate.now()));
    }
}
