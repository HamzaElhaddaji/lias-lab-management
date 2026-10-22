package com.lias.lias_backend.admission;
import com.lias.lias_backend.member.entity.*;
import com.lias.lias_backend.member.repository.*;
import com.lias.lias_backend.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.*;
import java.nio.file.*;
import java.time.*;
import java.util.*;
@RestController @RequiredArgsConstructor
public class AdmissionController {
 private final AdmissionRepository admissions;private final UserRepository users;private final MemberRepository members;
 private final AffiliationRepository affiliations;private final PasswordEncoder encoder;private final NotificationService notifications;
 @Value("${app.upload.dir:uploads}") private String uploadDir;
 @PostMapping(value="/api/public/applications",consumes=MediaType.MULTIPART_FORM_DATA_VALUE) @Transactional
 public Map<String,String> apply(@RequestParam String email,@RequestParam String password,@RequestParam String firstName,@RequestParam String lastName,
 @RequestParam String establishment,@RequestParam String originLaboratory,@RequestParam String motivation,
 @RequestParam(defaultValue="PERMANENT") Member.MemberStatus memberStatus,@RequestParam(defaultValue="") String team,@RequestParam MultipartFile cv)throws Exception {
  email=email.trim().toLowerCase(Locale.ROOT);
  if(!email.matches("^[^ @]+@[^ @]+[.][^ @]+$")||password.length()<8||firstName.isBlank()||lastName.isBlank()||establishment.isBlank()||originLaboratory.isBlank()||motivation.isBlank()||motivation.length()>4000) throw new IllegalArgumentException("Complete all fields; password must have at least eight characters");
  if(!List.of(Member.MemberStatus.PERMANENT,Member.MemberStatus.ASSOCIATE,Member.MemberStatus.DOCTORAL).contains(memberStatus)) throw new IllegalArgumentException("Invalid applicant status");
  if(users.existsByEmail(email)||admissions.existsByEmailIgnoreCaseAndDecision(email,"PENDING")) throw new IllegalArgumentException("An account or pending application already exists for this email");
  if(cv.isEmpty()||cv.getSize()>5*1024*1024||!"application/pdf".equals(cv.getContentType())) throw new IllegalArgumentException("CV must be a PDF under 5 MB");
  byte[] bytes=cv.getBytes();if(bytes.length<5||!new String(bytes,0,5,java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-")) throw new IllegalArgumentException("Invalid PDF");
  Path folder=Paths.get(uploadDir,"applications");Files.createDirectories(folder);String stored=UUID.randomUUID()+".pdf";Files.write(folder.resolve(stored),bytes);
  var a=new Admission();a.setEmail(email);a.setPasswordHash(encoder.encode(password));a.setFirstName(firstName);a.setLastName(lastName);a.setEstablishment(establishment);a.setOriginLaboratory(originLaboratory);a.setMotivation(motivation);a.setMemberStatus(memberStatus);a.setTeam(team);a.setCvPath(stored);a.setCvName(cv.getOriginalFilename());admissions.save(a);
  notifications.notifyNewMemberPending(email);return Map.of("message","Application submitted. The active director will review it; your account is created only after approval.");
 }
 @GetMapping("/api/admissions") @PreAuthorize("hasRole('DIRECTOR')") @Transactional(readOnly=true)
 public List<Map<String,Object>> list(){return admissions.findAll().stream().map(a->{var m=new LinkedHashMap<String,Object>();m.put("id",a.getId());m.put("email",a.getEmail());m.put("firstName",a.getFirstName());m.put("lastName",a.getLastName());m.put("establishment",a.getEstablishment());m.put("originLaboratory",a.getOriginLaboratory());m.put("motivation",a.getMotivation());m.put("memberStatus",a.getMemberStatus());m.put("team",a.getTeam());m.put("decision",a.getDecision());m.put("decisionReason",a.getDecisionReason());return (Map<String,Object>)m;}).toList();}
 @GetMapping("/api/admissions/{id}/cv") @PreAuthorize("hasRole('DIRECTOR')")
 public ResponseEntity<byte[]> cv(@PathVariable Long id)throws Exception {var a=admissions.findById(id).orElseThrow();return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).header("Content-Disposition","attachment; filename=CV.pdf").body(Files.readAllBytes(Paths.get(uploadDir,"applications",a.getCvPath())));}
 @PatchMapping("/api/admissions/{id}/decision") @PreAuthorize("hasRole('DIRECTOR')") @Transactional
 public Map<String,String> decide(@PathVariable Long id,@RequestBody Map<String,String> body){
  var a=admissions.findById(id).orElseThrow();if(!a.getDecision().equals("PENDING"))throw new IllegalArgumentException("Application already decided");
  String decision=body.get("decision");if(!List.of("APPROVED","REJECTED").contains(decision))throw new IllegalArgumentException("Invalid decision");
  a.setDecision(decision);a.setDecisionAt(LocalDateTime.now());a.setDecisionBy(SecurityContextHolder.getContext().getAuthentication().getName());a.setDecisionReason(body.getOrDefault("reason",""));
  if(decision.equals("APPROVED")){
   var role=a.getMemberStatus()==Member.MemberStatus.DOCTORAL?User.UserRole.DOCTORAL:User.UserRole.MEMBER;
   var u=users.save(User.builder().email(a.getEmail()).password(a.getPasswordHash()).role(role).status(User.UserStatus.ACTIVE).build());
   var m=members.save(Member.builder().user(u).firstName(a.getFirstName()).lastName(a.getLastName()).status(a.getMemberStatus()).hireDate(LocalDate.now()).establishment(a.getEstablishment()).originLaboratory(a.getOriginLaboratory()).build());
   affiliations.save(Affiliation.builder().member(m).laboratory("LIAS").team(a.getTeam()).startDate(LocalDate.now()).build());notifications.notifyMemberApproved(m);
  }else notifications.notifyApplicationRejected(a.getEmail(),a.getDecisionReason());
  admissions.save(a);return Map.of("message",decision);
 }
}
