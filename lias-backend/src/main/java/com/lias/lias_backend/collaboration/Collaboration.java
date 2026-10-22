package com.lias.lias_backend.collaboration;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import com.lias.lias_backend.member.entity.BaseEntity;
import java.time.LocalDate;
@Entity @Table(name="collaborations") @Audited @Getter @Setter @NoArgsConstructor
public class Collaboration extends BaseEntity {
 private String partner;private String title;private String contactEmail;private LocalDate startDate;private LocalDate endDate;
 @Column(length=4000) private String activities;
 private Long documentId;private String status="ACTIVE";
}
