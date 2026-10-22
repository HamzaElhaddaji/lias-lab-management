package com.lias.lias_backend.collaboration;
import com.lias.lias_backend.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RestController @RequestMapping("/api/collaborations") @RequiredArgsConstructor
public class CollaborationController {
 private final CollaborationRepository repository;private final DocumentRepository documents;
 public record Input(String partner,String title,String contactEmail,java.time.LocalDate startDate,java.time.LocalDate endDate,String activities,Long documentId,String status){}
 @GetMapping public List<Collaboration> list(){return repository.findAll();}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')") @Transactional
 public Collaboration create(@RequestBody Input input){return save(new Collaboration(),input);}
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')") @Transactional
 public Collaboration update(@PathVariable Long id,@RequestBody Input input){return save(repository.findById(id).orElseThrow(),input);}
 private Collaboration save(Collaboration c,Input i){
  if(i.partner()==null||i.partner().isBlank()||i.title()==null||i.title().isBlank()||i.startDate()==null)throw new IllegalArgumentException("Partner, title and start date are required");
  if(i.endDate()!=null&&i.endDate().isBefore(i.startDate()))throw new IllegalArgumentException("End date precedes start date");
  if(i.documentId()!=null&&!documents.existsById(i.documentId()))throw new IllegalArgumentException("Agreement document not found");
  if(i.status()!=null&&!List.of("ACTIVE","COMPLETED","ARCHIVED").contains(i.status()))throw new IllegalArgumentException("Invalid status");
  c.setPartner(i.partner());c.setTitle(i.title());c.setContactEmail(i.contactEmail());c.setStartDate(i.startDate());c.setEndDate(i.endDate());c.setActivities(i.activities());c.setDocumentId(i.documentId());if(i.status()!=null)c.setStatus(i.status());return repository.save(c);
 }
}
