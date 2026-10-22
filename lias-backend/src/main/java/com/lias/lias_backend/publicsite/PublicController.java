package com.lias.lias_backend.publicsite;
import com.lias.lias_backend.member.repository.*;
import com.lias.lias_backend.event.service.EventService;
import com.lias.lias_backend.publication.service.PublicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/public") @RequiredArgsConstructor
public class PublicController {
 private final MemberRepository members;private final AffiliationRepository affiliations;private final EventService events;private final PublicationService publications;
 @Value("${app.lab.creation-date:}") private String creationDate;
 @GetMapping("/laboratory") public Map<String,String> lab(){return Map.of("name","LIAS","institution","Faculte des Sciences Ben M'Sik, Universite Hassan II, Casablanca","description","Laboratoire d'Informatique et Applications des Sciences","creationDate",creationDate);}
 @GetMapping("/teams") @Transactional(readOnly=true) public List<Map<String,Object>> teams(){return affiliations.findAll().stream().filter(a->a.getEndDate()==null&&a.getTeam()!=null&&!a.getTeam().isBlank()).collect(java.util.stream.Collectors.groupingBy(a->a.getTeam())).entrySet().stream().map(e->Map.<String,Object>of("name",e.getKey(),"members",e.getValue().stream().map(a->Map.of("id",a.getMember().getId(),"name",String.valueOf(a.getMember().getFirstName())+" "+String.valueOf(a.getMember().getLastName()))).toList())).toList();}
 @GetMapping("/events") public Object events(){return events.getAllEvents();}
 @GetMapping("/publications") public Object publications(){return publications.getAllPublications();}
}
