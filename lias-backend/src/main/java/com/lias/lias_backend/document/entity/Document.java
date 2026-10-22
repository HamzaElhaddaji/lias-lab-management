package com.lias.lias_backend.document.entity;

import com.lias.lias_backend.event.entity.Event;
import com.lias.lias_backend.member.entity.BaseEntity;
import com.lias.lias_backend.member.entity.Member;
import jakarta.persistence.*;
import org.hibernate.envers.Audited;
import lombok.*;

@org.hibernate.annotations.SQLDelete(sql = "UPDATE documents SET archived = true WHERE id = ?")
@org.hibernate.annotations.SQLRestriction("archived = false")
@Audited
@Entity
@Table(name = "documents")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Document extends BaseEntity {

    @Column(nullable = false)
    private String fileName;

    @Column(nullable = false)
    private String filePath;

    private String fileType;
    private Long fileSize;

    @Enumerated(EnumType.STRING)
    private DocumentType type;

    @ManyToOne
    @JoinColumn(name = "uploaded_by")
    private Member uploadedBy;

    @ManyToOne
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(length = 4000)
    private String description;

    public enum DocumentType {
        FUNDING_REQUEST, PROGRAM, CERTIFICATE, REPORT, ADMINISTRATIVE, OTHER
    }
}
