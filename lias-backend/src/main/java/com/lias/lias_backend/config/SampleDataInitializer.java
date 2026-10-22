package com.lias.lias_backend.config;

import com.lias.lias_backend.member.entity.*;
import com.lias.lias_backend.member.repository.*;
import com.lias.lias_backend.publication.entity.Publication;
import com.lias.lias_backend.event.entity.Event;
import com.lias.lias_backend.document.entity.Document;
import com.lias.lias_backend.meeting.entity.Meeting;
import com.lias.lias_backend.equipment.entity.*;
import com.lias.lias_backend.governance.entity.Mandate;
import com.lias.lias_backend.collaboration.Collaboration;
import com.lias.lias_backend.communication.DiscussionMessage;
import com.lias.lias_backend.admission.Admission;
import com.lias.lias_backend.notification.entity.Notification;
import com.itextpdf.kernel.pdf.*;
import com.itextpdf.layout.element.Paragraph;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.*;

/** Fictional, opt-in database content. Never sends email or replaces existing records. */
@Component @Order(10) @RequiredArgsConstructor
@ConditionalOnProperty(name="app.seed.sample-data", havingValue="true")
public class SampleDataInitializer implements CommandLineRunner {
 private final EntityManager em;
 private final MemberRepository members;
 private final UserRepository users;
 private final PasswordEncoder encoder;
 @Value("${app.seed.enabled:false}") private boolean enabled;
 @Value("${app.seed.password:FriendDemo2026!}") private String password;
 @Value("${app.upload.dir:uploads}") private String uploadDir;

 @Override @Transactional
 public void run(String... args) throws Exception {
  if(!enabled || users.existsByEmail("amina.elidrissi@example.test")) return;
  LocalDate today=LocalDate.now(); int year=today.getYear();
  String hash=encoder.encode(password);
  String[][] people={
   {"Amina","El Idrissi","amina.elidrissi","ISDIAC","Machine learning and responsible AI"},
   {"Youssef","Berrada","youssef.berrada","ILIAS","Cloud systems and distributed computing"},
   {"Sara","Benali","sara.benali","ISDIAC","Medical imaging and computer vision"},
   {"Omar","Alaoui","omar.alaoui","ILIAS","Cybersecurity and privacy"},
   {"Lina","Rahmani","lina.rahmani","Data & Knowledge","Natural language processing"},
   {"Mehdi","El Amrani","mehdi.elamrani","Data & Knowledge","Knowledge graphs and data engineering"},
   {"Ines","Chafai","ines.chafai","ISDIAC","Explainable neural networks"},
   {"Adam","Nouri","adam.nouri","ILIAS","Internet of Things and edge computing"},
   {"Nadia","Bensalem","nadia.bensalem","Data & Knowledge","Information retrieval and digital libraries"},
   {"Samir","Tazi","samir.tazi","ISDIAC","Scientific computing and optimization"}
  };
  List<Member> staff=new ArrayList<>();
  for(int i=0;i<people.length;i++) {
   var p=people[i];var status=i==6||i==7?Member.MemberStatus.DOCTORAL:i==8?Member.MemberStatus.ASSOCIATE:Member.MemberStatus.PERMANENT;
   var user=User.builder().email(p[2]+"@example.test").password(hash).role(status==Member.MemberStatus.DOCTORAL?User.UserRole.DOCTORAL:User.UserRole.MEMBER).status(User.UserStatus.ACTIVE).build();em.persist(user);
   var member=Member.builder().user(user).firstName(p[0]).lastName(p[1]).status(status).hireDate(today.minusYears(2+i%5)).birthDate(LocalDate.of(1980+i*2,3+i%8,12)).establishment("Sample University of Casablanca").originLaboratory(status==Member.MemberStatus.ASSOCIATE?"Atlas Research Lab (fictional)":"LIAS").biography("Fictional demonstration member. Research focuses on "+p[4].toLowerCase()+" and interdisciplinary collaboration.").interests(p[4]).build();em.persist(member);staff.add(member);
   em.persist(Affiliation.builder().member(member).laboratory("LIAS").team(p[3]).startDate(today.minusYears(1+i%3)).build());
  }
  Member director=members.findByUserEmail("director@lias.ma").orElseThrow();
  Member admin=members.findByUserEmail("admin@lias.ma").orElseThrow();
  Member standard=members.findByUserEmail("member@lias.ma").orElseThrow();
  Member doctoral=members.findByUserEmail("doctorant@lias.ma").orElseThrow();
  em.persist(Mandate.builder().member(staff.get(0)).role(Mandate.MandateRole.VICE_DIRECTOR).startDate(today.minusMonths(3)).endDate(today.plusYears(2)).build());
  for(int i:new int[]{1,2,4}) em.persist(Mandate.builder().member(staff.get(i)).role(Mandate.MandateRole.TEAM_LEADER).team(people[i][3]).startDate(today.minusMonths(4)).endDate(today.plusYears(1)).build());

  String[] titles={"Explainable AI for Predictive Maintenance","Privacy-Preserving Learning in Smart Cities","Arabic Text Classification with Transformer Models","Energy-Aware Scheduling for Edge Devices","Knowledge Graphs for Scientific Discovery","Deep Learning for Medical Image Segmentation","Secure Data Sharing in Distributed Research Systems","A Benchmark for Multilingual Information Retrieval","Graph Neural Networks for Traffic Prediction","Federated Learning Across Heterogeneous Devices","Digital Twins for Sustainable Manufacturing","An Open Research Data Management Framework","Optimizing Cloud Resources with Reinforcement Learning","Robust Anomaly Detection in Sensor Networks","Human-Centered Evaluation of AI Assistants","Semantic Search for Institutional Archives"};
  for(int i=0;i<titles.length;i++) {
   Member author=i==14?doctoral:i==15?standard:staff.get(i%staff.size());
   em.persist(Publication.builder().member(author).title(titles[i]).authors(author.getFirstName()+" "+author.getLastName()+", "+staff.get((i+2)%staff.size()).getFirstName()+" "+staff.get((i+2)%staff.size()).getLastName()).year(year-i%3).team(i==14?"ILIAS":i==15?"ISDIAC":people[i%people.length][3]).type(i%4==0?Publication.PublicationType.CONFERENCE:Publication.PublicationType.JOURNAL).journal(i%4==0?null:"LIAS Demonstration Research Series").conference(i%4==0?"Sample Symposium on Computing and Data":"").abstractText("Fictional sample publication for demonstrating the LIAS platform. This record is not a real published research result. Topic: "+titles[i]+".").build());
  }
  String[] eventNames={"Research Methods and Reproducibility Seminar","Workshop on Responsible Artificial Intelligence","LIAS Doctoral Research Day","Scientific Writing and Publication Clinic","Seminar: Knowledge Graphs in Practice","Workshop: Cloud and Edge Computing","LIAS Annual Research Conference","Industry and Research Collaboration Forum"};
  int[] offsets={-42,-21,-7,1,5,12,28,45};List<Event> events=new ArrayList<>();
  for(int i=0;i<eventNames.length;i++) {
   var event=Event.builder().title(eventNames[i]).type(i==6?Event.EventType.CONFERENCE:i%2==0?Event.EventType.SEMINAR:Event.EventType.WORKSHOP).description("Fictional sample event. Presentations, practical sessions and discussion with laboratory members.").location(i%2==0?"LIAS Conference Room":"Research Training Room B").edition(String.valueOf(year)).startDate(today.plusDays(offsets[i])).endDate(today.plusDays(offsets[i]+(i==6?1:0))).status(offsets[i]<0?Event.EventStatus.COMPLETED:Event.EventStatus.PLANNED).organizer(staff.get(i%staff.size())).build();
   event.getOrganizers().add(staff.get(i%staff.size()));event.getOrganizers().add(director);em.persist(event);events.add(event);
  }
  List<Document> docs=new ArrayList<>();String[] documentNames={"research-day-program.pdf","sample-funding-request.pdf","annual-research-summary.pdf","workshop-attendance-certificate.pdf","collaboration-agreement.pdf","laboratory-equipment-policy.pdf"};
  Document.DocumentType[] documentTypes={Document.DocumentType.PROGRAM,Document.DocumentType.FUNDING_REQUEST,Document.DocumentType.REPORT,Document.DocumentType.CERTIFICATE,Document.DocumentType.ADMINISTRATIVE,Document.DocumentType.ADMINISTRATIVE};
  for(int i=0;i<documentNames.length;i++) {
   String path="samples/"+documentNames[i];Path file=pdf(path,"LIAS - "+documentNames[i].replace("-"," "),"Sample demonstration document. This fictional file illustrates the laboratory archive.\nPrepared for the LIAS demonstration dataset.\nCategory: "+documentTypes[i]+".\nNo real approval, funding award or certification is represented.");
   var doc=Document.builder().fileName(documentNames[i]).filePath(path).fileType("application/pdf").fileSize(Files.size(file)).type(documentTypes[i]).description("Fictional demonstration document with downloadable PDF content.").uploadedBy(i%2==0?director:staff.get(0)).event(i<4?events.get(i):null).build();em.persist(doc);docs.add(doc);
  }
  String[] meetingNames={"Laboratory Council Meeting","Research Team Coordination","Doctoral Supervision Roundtable","Annual Report Planning","Equipment Distribution Committee"};
  for(int i=0;i<meetingNames.length;i++) {
   var meeting=Meeting.builder().title(meetingNames[i]).date(today.plusDays(i<2?-14+i*7:3+i*4)).location("LIAS Meeting Room").agenda("1. Progress review\n2. Research priorities\n3. Resource planning\n4. Decisions and next steps").description("Fictional sample meeting for demonstrating agendas and minutes.").status(i<2?Meeting.MeetingStatus.COMPLETED:Meeting.MeetingStatus.PLANNED).createdBy(director).build();
   if(i<2){String name="sample-meeting-"+(i+1)+"-minutes.pdf";pdf("pv/"+name,meetingNames[i]+" - Minutes","SAMPLE MINUTES - Fictional demonstration.\nDiscussion: research milestones, publications and laboratory resources.\nDecision: prepare a shared seminar program and review equipment requests at the next meeting.");meeting.setPvFilePath("pv/"+name);meeting.setPvFileName(name);}
   em.persist(meeting);
  }
  String[] equipmentNames={"Research Laptop","27-inch Monitor","GPU Workstation","Conference Projector","Network Storage Unit","Raspberry Pi Development Kit","Video Conference Camera"};int[] quantities={8,10,2,2,1,12,3};List<Equipment> equipment=new ArrayList<>();
  for(int i=0;i<equipmentNames.length;i++) {int assigned=i==0?3:i==1?2:i==5?2:0;var e=Equipment.builder().name(equipmentNames[i]).description("Fictional sample inventory for research and training.").serialNumber("SAMPLE-"+year+"-"+String.format("%03d",i+1)).quantity(quantities[i]).availableQuantity(quantities[i]-assigned).arrivalDate(today.minusDays(20+i*8)).condition(i%3==0?Equipment.EquipmentCondition.NEW:Equipment.EquipmentCondition.GOOD).status(assigned>0?Equipment.EquipmentStatus.PARTIALLY_ASSIGNED:Equipment.EquipmentStatus.AVAILABLE).notes("Sample batch; no real asset ownership is asserted.").build();em.persist(e);equipment.add(e);}
  int[] recipients={0,1,2,3,4,5};int[] itemIndices={0,0,0,1,1,5};
  for(int i=0;i<recipients.length;i++) em.persist(EquipmentAssignment.builder().equipment(equipment.get(itemIndices[i])).member(staff.get(recipients[i])).quantityAssigned(i==5?2:1).assignmentDate(today.minusDays(15-i)).assignmentNote("Sample allocation for research project.").build());
  em.persist(EquipmentAssignment.builder().equipment(equipment.get(6)).member(staff.get(3)).quantityAssigned(1).assignmentDate(today.minusDays(30)).returnDate(today.minusDays(10)).assignmentNote("Sample conference loan").returnNote("Returned in good condition").build());
  for(int i=0;i<5;i++) em.persist(EquipmentRequest.builder().requestedBy(i==4?standard:staff.get(i)).equipmentName(equipmentNames[(i+1)%equipmentNames.length]).quantityRequested(1).justification("Sample request to support experimental work and upcoming project milestones.").requestDate(today.minusDays(5-i)).status(i<3?EquipmentRequest.RequestStatus.PENDING:i==3?EquipmentRequest.RequestStatus.APPROVED:EquipmentRequest.RequestStatus.REJECTED).validatedBy(i>=3?director:null).validationDate(i>=3?today.minusDays(1):null).validationNote(i>=3?"Fictional demonstration decision":null).build());
  String[] partners={"Atlas Innovation Institute (fictional)","Mediterranean Data Research Center (fictional)","Casablanca Digital Industry Hub (fictional)"};
  for(int i=0;i<partners.length;i++){var collaboration=new Collaboration();collaboration.setPartner(partners[i]);collaboration.setTitle(new String[]{"Joint AI Research Program","Doctoral Mobility and Co-supervision","Applied Research and Technology Transfer"}[i]);collaboration.setContactEmail("research"+i+"@example.test");collaboration.setStartDate(today.minusMonths(2+i));collaboration.setEndDate(today.plusYears(1+i));collaboration.setActivities("Fictional collaboration: joint seminars, shared datasets and student research exchanges.");collaboration.setDocumentId(docs.get(4).getId());em.persist(collaboration);}
  String[] messages={"Welcome to the LIAS demonstration workspace. All records in this sample dataset are fictional.","The scientific writing clinic is coming up. Please bring a draft abstract for the practical session.","Our team is preparing experiments for the next research review.","Please share reproducibility notes and dataset documentation in the archive.","The workshop program and attendance certificate are available in Documents.","Equipment requests will be reviewed at the next committee meeting.","We have added the sample seminar agenda. Suggestions for discussion topics are welcome.","The annual report will include publications, events and shared laboratory activities."};
  for(int i=0;i<messages.length;i++){var message=new DiscussionMessage();message.setAuthor(i%3==0?director:staff.get(i%staff.size()));message.setContent(messages[i]);if(i==2||i==3)message.setTeam("ISDIAC");if(i==6)message.setEventId(events.get(4).getId());em.persist(message);}
  for(int i=0;i<3;i++){var application=new Admission();application.setEmail("sample.applicant"+(i+1)+"@example.test");application.setPasswordHash(hash);application.setFirstName(new String[]{"Salma","Rayan","Hiba"}[i]);application.setLastName(new String[]{"Amine","Khalil","Mansouri"}[i]);application.setEstablishment("Sample Faculty of Sciences");application.setOriginLaboratory("Example Research Group");application.setTeam(i==1?"ILIAS":"ISDIAC");application.setMemberStatus(i==1?Member.MemberStatus.DOCTORAL:Member.MemberStatus.ASSOCIATE);application.setMotivation("Fictional sample application: interested in joining collaborative research on data science and intelligent systems.");String name="sample-cv-"+(i+1)+".pdf";pdf("applications/"+name,"Sample applicant CV","FICTIONAL CV for demonstration purposes.\nResearch interests: machine learning, data systems and reproducible science.\nEducation and experience shown here do not represent a real person.");application.setCvPath(name);application.setCvName(name);em.persist(application);}
  var recipientsList=new ArrayList<>(staff);recipientsList.addAll(List.of(director,admin,standard,doctoral));
  for(Member recipient:recipientsList) for(int i=0;i<3;i++) em.persist(Notification.builder().member(recipient).title(new String[]{"Welcome to the sample workspace","Upcoming scientific writing clinic","New documents in the archive"}[i]).message(new String[]{"Explore the fictional sample records across the laboratory modules.","The calendar contains upcoming seminars and workshops.","Sample programs, reports and meeting minutes can be downloaded."}[i]).type(i==1?Notification.NotificationType.NEW_EVENT:Notification.NotificationType.NEW_DOCUMENT).read(i==0).build());
  em.flush();
 }

 private Path pdf(String relative,String title,String body)throws Exception {
  Path file=Paths.get(uploadDir).resolve(relative);Files.createDirectories(file.getParent());
  if(!Files.exists(file))try(var document=new com.itextpdf.layout.Document(new PdfDocument(new PdfWriter(file.toString())))) {
   document.add(new Paragraph("FICTIONAL SAMPLE - LIAS DEMONSTRATION").setBold().setFontSize(10));document.add(new Paragraph(title).setFontSize(20));
   for(String paragraph:body.split("\\n"))document.add(new Paragraph(paragraph));
  }return file;
 }
}
