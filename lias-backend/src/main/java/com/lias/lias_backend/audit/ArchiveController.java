package com.lias.lias_backend.audit;
import jakarta.persistence.EntityManager;
import com.lias.lias_backend.member.entity.BaseEntity;
import com.lias.lias_backend.document.entity.Document;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.*;
import java.nio.file.*;
import java.util.*;

@RestController @RequestMapping("/api/archive") @RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','DIRECTOR')")
public class ArchiveController {
 private final EntityManager em;
 @Value("${app.upload.dir:uploads}") private String uploadDir;
 private static final Map<String,String> TYPES=Map.of("events","event.entity.Event","documents","document.entity.Document","equipment","equipment.entity.Equipment","meetings","meeting.entity.Meeting","publications","publication.entity.Publication","mandates","governance.entity.Mandate");
 private Class<?> type(String kind)throws Exception {if(!TYPES.containsKey(kind))throw new IllegalArgumentException("Unknown archive category");return Class.forName("com.lias.lias_backend."+TYPES.get(kind));}
 @GetMapping("/{kind}") @Transactional(readOnly=true)
 public List<Map<String,Object>> list(@PathVariable String kind)throws Exception {
  type(kind);String label=kind.equals("documents")?"file_name":kind.equals("equipment")?"name":kind.equals("mandates")?"cast(role as varchar)":"title";
  @SuppressWarnings("unchecked") var records=(List<Object[]>)em.createNativeQuery("SELECT id, "+label+", updated_at FROM "+kind+" WHERE archived = true ORDER BY id DESC",Object[].class).getResultList();
  return records.stream().map(r->{var dto=new LinkedHashMap<String,Object>();dto.put("id",r[0]);dto.put("title",r[1]);dto.put("archivedAt",r[2]==null?null:r[2].toString());return (Map<String,Object>)dto;}).toList();
 }
 @PatchMapping("/{kind}/{id}/restore") @Transactional
 public Map<String,String> restore(@PathVariable String kind,@PathVariable Long id)throws Exception {
  if(kind.equals("mandates") && org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream().noneMatch(a->a.getAuthority().equals("ROLE_ADMIN")))
   throw new org.springframework.security.access.AccessDeniedException("Only administrators may restore mandates");
  var entity=(BaseEntity)em.createNativeQuery("SELECT * FROM "+safe(kind)+" WHERE id=:id AND archived=true",type(kind)).setParameter("id",id).getSingleResult();
  entity.setArchived(false);em.flush();return Map.of("message","Restored");
 }
 private String safe(String kind)throws Exception {type(kind);return kind;}
 @GetMapping("/documents/{id}/download") @Transactional(readOnly=true)
 public ResponseEntity<byte[]> download(@PathVariable Long id)throws Exception {
  var doc=(Document)em.createNativeQuery("SELECT * FROM documents WHERE id=:id AND archived=true",Document.class).setParameter("id",id).getSingleResult();
  return ResponseEntity.ok().contentType(MediaType.APPLICATION_OCTET_STREAM).header("Content-Disposition","attachment; filename=archived-document").body(Files.readAllBytes(Paths.get(uploadDir).resolve(doc.getFilePath())));
 }
}
