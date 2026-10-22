package com.lias.lias_backend.security.service;
import com.lias.lias_backend.member.entity.*;
import com.lias.lias_backend.member.entity.User;
import com.lias.lias_backend.member.repository.*;
import com.lias.lias_backend.governance.repository.MandateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
 private final UserRepository users;private final MemberRepository members;private final MandateRepository mandates;
 @Override @Transactional(readOnly=true)
 public UserDetails loadUserByUsername(String email) {
  var user=users.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("Account not found"));
  if(user.getStatus()!=User.UserStatus.ACTIVE) throw new DisabledException("Account is pending, frozen or disabled");
  var member=members.findByUserId(user.getId()).orElseThrow(()->new DisabledException("Member not found"));
  if(member.getStatus()==Member.MemberStatus.RETIRED||member.getStatus()==Member.MemberStatus.FORMER) throw new DisabledException("Account is inactive");
  var roles=new HashSet<String>();
  if(member.getStatus()==Member.MemberStatus.DOCTORAL) roles.add("DOCTORAL");
  else if(member.getStatus()==Member.MemberStatus.ASSOCIATE) { roles.add("ASSOCIATE"); if(member.isPublicationContributor()) roles.add("ASSOCIATE_PUBLICATIONS"); }
  else {
   roles.add("MEMBER");
   if(user.getRole()==User.UserRole.ADMIN) roles.add("ADMIN");
   mandates.findActiveByMemberId(member.getId()).forEach(m->roles.add(m.getRole().name()));
  }
  return new org.springframework.security.core.userdetails.User(email,user.getPassword(),roles.stream().map(r->new SimpleGrantedAuthority("ROLE_"+r)).toList());
 }
}
