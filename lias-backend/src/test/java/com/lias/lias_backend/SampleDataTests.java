package com.lias.lias_backend;
import com.lias.lias_backend.config.SampleDataInitializer;
import com.lias.lias_backend.member.repository.MemberRepository;
import com.lias.lias_backend.publication.repository.PublicationRepository;
import com.lias.lias_backend.event.repository.EventRepository;
import com.lias.lias_backend.document.repository.DocumentRepository;
import com.lias.lias_backend.equipment.repository.EquipmentRepository;
import com.lias.lias_backend.meeting.repository.MeetingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest(properties={
 "spring.datasource.url=jdbc:h2:mem:samples;NON_KEYWORDS=YEAR,CONDITION,READ;DB_CLOSE_DELAY=-1",
 "spring.datasource.driver-class-name=org.h2.Driver","spring.datasource.username=sa","spring.datasource.password=",
 "spring.jpa.hibernate.ddl-auto=create-drop","app.seed.enabled=true","app.seed.sample-data=true","app.mail.enabled=false","app.upload.dir=target/sample-test-uploads"
})
class SampleDataTests {
 @Autowired SampleDataInitializer samples;
 @Autowired MemberRepository members;
 @Autowired PublicationRepository publications;
 @Autowired EventRepository events;
 @Autowired DocumentRepository documents;
 @Autowired EquipmentRepository equipment;
 @Autowired MeetingRepository meetings;
 @Test void seedsFictionalRecordsAndDoesNotDuplicateThem()throws Exception {
  assertEquals(14,members.count());assertEquals(16,publications.count());assertEquals(8,events.count());assertEquals(6,documents.count());assertEquals(7,equipment.count());assertEquals(5,meetings.count());
  for(var doc:documents.findAll()){var file=java.nio.file.Path.of("target/sample-test-uploads").resolve(doc.getFilePath());assertTrue(java.nio.file.Files.exists(file));assertTrue(new String(java.nio.file.Files.readAllBytes(file),0,5,java.nio.charset.StandardCharsets.US_ASCII).equals("%PDF-"));}
  samples.run();assertEquals(14,members.count());assertEquals(16,publications.count());assertEquals(8,events.count());
 }
}
