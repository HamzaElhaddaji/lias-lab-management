package com.lias.lias_backend.config;
import com.lias.lias_backend.security.jwt.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.*;
import java.util.List;
@Configuration @EnableMethodSecurity @RequiredArgsConstructor
public class SecurityConfig {
 private final JwtAuthFilter jwtAuthFilter;
 @Value("${app.cors.allowed-origins:http://localhost:5173}") private List<String> origins;
 @Bean SecurityFilterChain filterChain(HttpSecurity http)throws Exception {
  return http.cors(c->c.configurationSource(corsConfigurationSource())).csrf(c->c.disable())
   .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
   .authorizeHttpRequests(a->a
    .requestMatchers("/api/auth/**","/api/public/**","/api/health").permitAll()
    .requestMatchers("/api/admissions/**").hasRole("DIRECTOR")
    .requestMatchers("/api/admin/**","/api/history/**","/api/report/**").hasAnyRole("ADMIN","DIRECTOR")
    .requestMatchers("/api/members/me","/api/members/me/photo","/api/members/me/account","/api/notifications/**").authenticated()
    .requestMatchers("/api/publications/**").authenticated()
    .requestMatchers(HttpMethod.GET,"/api/events/**").hasAnyRole("MEMBER","ASSOCIATE","ADMIN","DIRECTOR","VICE_DIRECTOR","TEAM_LEADER")
    .requestMatchers("/api/**").hasAnyRole("MEMBER","ADMIN","DIRECTOR","VICE_DIRECTOR","TEAM_LEADER")
    .anyRequest().denyAll())
   .exceptionHandling(e->e
    .authenticationEntryPoint((q,r,x)->{r.setStatus(401);r.setContentType("application/json");r.getWriter().write("{\"message\":\"Authentication required\"}");})
    .accessDeniedHandler((q,r,x)->{r.setStatus(403);r.setContentType("application/json");r.getWriter().write("{\"message\":\"Access denied for this role\"}");}))
   .addFilterBefore(jwtAuthFilter,UsernamePasswordAuthenticationFilter.class).build();
 }
 @Bean CorsConfigurationSource corsConfigurationSource(){var c=new CorsConfiguration();c.setAllowedOrigins(origins);c.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));c.setAllowedHeaders(List.of("*"));c.setAllowCredentials(true);var s=new UrlBasedCorsConfigurationSource();s.registerCorsConfiguration("/**",c);return s;}
 @Bean PasswordEncoder passwordEncoder(){return new BCryptPasswordEncoder();}
 @Bean AuthenticationManager authenticationManager(AuthenticationConfiguration c)throws Exception{return c.getAuthenticationManager();}
}
