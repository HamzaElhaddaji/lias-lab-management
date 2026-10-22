package com.lias.lias_backend.communication;
import com.lias.lias_backend.member.repository.*;
import com.lias.lias_backend.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@RestController @RequestMapping("/api/discussions") @RequiredArgsConstructor
public class DiscussionController {
 private final DiscussionRepository messages;private final MemberRepository members;private final AffiliationRepository affiliations;private final EventRepository events;
 public record MessageInput(String content,String team,Long eventId){}
 private void check(String team,Long eventId){
  var auth=SecurityContextHolder.getContext().getAuthentication();
  if(eventId!=null&&!events.existsById(eventId))throw new IllegalArgumentException("Event not found");
  if(team==null||team.isBlank())return;
  boolean elevated=auth.getAuthorities().stream().anyMatch(a->List.of("ROLE_ADMIN","ROLE_DIRECTOR").contains(a.getAuthority()));
  var member=members.findByUserEmail(auth.getName()).orElseThrow();
  if(!elevated&&!affiliations.findByMemberIdAndEndDateIsNull(member.getId()).map(a->team.equals(a.getTeam())).orElse(false))throw new org.springframework.security.access.AccessDeniedException("This discussion is for another team");
 }
 private Map<String,Object> dto(DiscussionMessage m){var r=new LinkedHashMap<String,Object>();r.put("id",m.getId());r.put("content",m.getContent());r.put("author",m.getAuthor().getFirstName()+" "+m.getAuthor().getLastName());r.put("createdAt",m.getCreatedAt());r.put("team",m.getTeam());r.put("eventId",m.getEventId());return r;}
 @GetMapping @Transactional(readOnly=true) public List<Map<String,Object>> list(@RequestParam(required=false)String team,@RequestParam(required=false)Long eventId){check(team,eventId);return messages.findAll().stream().filter(m->Objects.equals(m.getTeam(),team==null||team.isBlank()?null:team)&&Objects.equals(m.getEventId(),eventId)).sorted(Comparator.comparing(DiscussionMessage::getCreatedAt)).map(this::dto).toList();}
 @PostMapping @Transactional public Map<String,Object> post(@RequestBody MessageInput input){check(input.team(),input.eventId());if(input.content()==null||input.content().isBlank()||input.content().length()>4000)throw new IllegalArgumentException("Message must contain 1-4000 characters");var m=new DiscussionMessage();m.setAuthor(members.findByUserEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow());m.setContent(input.content());m.setTeam(input.team()==null||input.team().isBlank()?null:input.team());m.setEventId(input.eventId());return dto(messages.saveAndFlush(m));}
}
