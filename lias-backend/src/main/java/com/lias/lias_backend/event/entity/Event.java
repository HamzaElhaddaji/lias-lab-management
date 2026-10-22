package com.lias.lias_backend.event.entity;

import com.lias.lias_backend.member.entity.BaseEntity;
import com.lias.lias_backend.member.entity.Member;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import lombok.*;

import java.time.LocalDate;

@org.hibernate.annotations.SQLDelete(sql = "UPDATE events SET archived = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("archived = false")
@Audited
@Entity
@Table(name = "events")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Event extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(length = 4000)
    private String description;
    private String location;

    @Column(nullable = false)
    private LocalDate startDate;

    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType type;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    // The member who created/organized this event
    @ManyToOne
    @JoinColumn(name = "organizer_id")
    private Member organizer;

    @ManyToMany
    @JoinTable(name = "event_organizers", joinColumns = @JoinColumn(name = "event_id"), inverseJoinColumns = @JoinColumn(name = "member_id"))
    @Builder.Default
    private java.util.Set<Member> organizers = new java.util.HashSet<>();

    private String edition;
    private String website;

    public enum EventType {
        CONFERENCE, SEMINAR, WORKSHOP, OTHER
    }

    public enum EventStatus {
        PLANNED, ONGOING, COMPLETED, CANCELLED
    }
}
