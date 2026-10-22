package com.lias.lias_backend.member.controller;
import com.lias.lias_backend.member.dto.AuthResponse;
import com.lias.lias_backend.member.repository.UserRepository;
import com.lias.lias_backend.security.jwt.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/members/me/account") @RequiredArgsConstructor
public class MemberAccountController {
 private final UserRepository users;private final PasswordEncoder encoder;private final JwtUtil tokens;
 public record AccountInput(@NotBlank @Email String email,@NotBlank String currentPassword){}
 @PutMapping @Transactional
 public AuthResponse update(@Valid @RequestBody AccountInput input){
  var user=users.findByEmail(SecurityContextHolder.getContext().getAuthentication().getName()).orElseThrow();
  if(!encoder.matches(input.currentPassword(),user.getPassword()))throw new org.springframework.security.authentication.BadCredentialsException("Incorrect password");
  String email=input.email().trim().toLowerCase(java.util.Locale.ROOT);
  if(!email.equals(user.getEmail()) && users.existsByEmail(email))throw new IllegalArgumentException("Email already in use");
  user.setEmail(email);users.save(user);return new AuthResponse(tokens.generateToken(email,user.getRole().name()),user.getRole().name(),email);
 }
}
