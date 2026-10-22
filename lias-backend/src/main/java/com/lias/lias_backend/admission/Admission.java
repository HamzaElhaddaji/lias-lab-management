package com.lias.lias_backend.admission;
import com.lias.lias_backend.member.entity.*;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.Audited;
import java.time.LocalDateTime;
@Entity @Table(name="admissions") @Audited @Getter @Setter @NoArgsConstructor
public class Admission extends BaseEntity {
 private String email;private String passwordHash;private String firstName;private String lastName;
 private String establishment;private String originLaboratory;private String team;
 @Column(length=4000) private String motivation;
 private String cvPath;private String cvName;
 @Enumerated(EnumType.STRING) private Member.MemberStatus memberStatus;
 private String decision="PENDING";private String decisionBy;private LocalDateTime decisionAt;
 @Column(length=2000) private String decisionReason;
}
