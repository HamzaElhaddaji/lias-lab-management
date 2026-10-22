package com.lias.lias_backend.member.entity;

import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import lombok.*;
import java.time.LocalDate;

@org.hibernate.annotations.SQLDelete(sql = "UPDATE members SET archived = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("archived = false")
@Audited
@Entity
@Table(name = "members")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Member extends BaseEntity {

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String firstName;
    private String lastName;
    private String photoPath;
    @Column(length = 1000)
    private String biography;
    @Column(length = 500)
    private String interests;
    private String establishment;
    private String originLaboratory;
    @Column(nullable = false, columnDefinition = "boolean default false")
    private boolean publicationContributor;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Enumerated(EnumType.STRING)
    private MemberStatus status;

    public enum MemberStatus {
        PERMANENT, ASSOCIATE, DOCTORAL, RETIRED, FORMER
    }
}
