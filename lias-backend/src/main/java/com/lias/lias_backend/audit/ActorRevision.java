package com.lias.lias_backend.audit;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.*;
@Entity @Table(name="audit_revisions") @RevisionEntity(ActorRevisionListener.class) @Getter @Setter
public class ActorRevision {
 @Id @GeneratedValue @RevisionNumber private int id;
 @RevisionTimestamp private long timestamp;
 private String actor;
}
