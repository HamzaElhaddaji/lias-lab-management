package com.lias.lias_backend.communication;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import com.lias.lias_backend.member.entity.*;
@Entity @Table(name="discussion_messages") @Audited @Getter @Setter @NoArgsConstructor
public class DiscussionMessage extends BaseEntity {
 @ManyToOne(optional=false) private Member author;
 private String team;private Long eventId;
 @Column(nullable=false,length=4000) private String content;
}
