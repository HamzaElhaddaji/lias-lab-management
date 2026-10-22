package com.lias.lias_backend.audit;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.hibernate.envers.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/history") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
public class HistoryController {
 private final EntityManager em;
 @GetMapping("/{kind}/{id}") @Transactional(readOnly=true)
 public List<Map<String,Object>> history(@PathVariable String kind,@PathVariable Long id) throws Exception {
  var classes=Map.of("members","member.entity.Member","users","member.entity.User","mandates","governance.entity.Mandate","documents","document.entity.Document","events","event.entity.Event","equipment","equipment.entity.Equipment","meetings","meeting.entity.Meeting","publications","publication.entity.Publication","affiliations","member.entity.Affiliation");
  if(!classes.containsKey(kind)) throw new IllegalArgumentException("Unknown history category");
  Class<?> type=Class.forName("com.lias.lias_backend."+classes.get(kind));
  var reader=AuditReaderFactory.get(em);var result=new ArrayList<Map<String,Object>>();
  for(Number n:reader.getRevisions(type,id)) {
   var revision=reader.findRevision(ActorRevision.class,n);Object entity=reader.find(type,id,n);
   var values=new LinkedHashMap<String,Object>();
   if(entity!=null) for(var field:type.getDeclaredFields()) {
    field.setAccessible(true);Object value=field.get(entity);
    if(field.getName().toLowerCase().matches(".*(password|path|birthdate).*")) continue;
    if(value==null||value instanceof String||value instanceof Number||value instanceof Boolean||value instanceof Enum<?>||value instanceof java.time.temporal.Temporal) values.put(field.getName(),value);
   }
   result.add(Map.of("revision",n,"actor",Optional.ofNullable(revision.getActor()).orElse("system"),"timestamp",revision.getTimestamp(),"values",values));
  }return result;
 }
}
