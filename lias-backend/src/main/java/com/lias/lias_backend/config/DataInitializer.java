package com.lias.lias_backend.config;

import com.lias.lias_backend.member.entity.Affiliation;
import com.lias.lias_backend.member.entity.Member;
import com.lias.lias_backend.member.entity.User;
import com.lias.lias_backend.member.repository.AffiliationRepository;
import com.lias.lias_backend.member.repository.MemberRepository;
import com.lias.lias_backend.member.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Component
@org.springframework.core.annotation.Order(0)
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MemberRepository memberRepository;
    private final AffiliationRepository affiliationRepository;
    private final PasswordEncoder passwordEncoder;
    private final com.lias.lias_backend.governance.repository.MandateRepository mandates;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${app.seed.password:FriendDemo2026!}")
    private String seedPassword;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedEnabled) {
            return;
        }

        seedUser("admin@lias.ma", "Admin", "LIAS", User.UserRole.ADMIN, Member.MemberStatus.PERMANENT, "Direction");
        seedUser("director@lias.ma", "Director", "LIAS", User.UserRole.DIRECTOR, Member.MemberStatus.PERMANENT, "Direction");
        seedUser("member@lias.ma", "Permanent", "Member", User.UserRole.MEMBER, Member.MemberStatus.PERMANENT, "ISDIAC");
        seedUser("doctorant@lias.ma", "Doctoral", "Student", User.UserRole.DOCTORAL, Member.MemberStatus.DOCTORAL, "ILIAS");
        var director=memberRepository.findByUserEmail("director@lias.ma").orElseThrow();
        if(mandates.findByMemberId(director.getId()).isEmpty()) mandates.save(com.lias.lias_backend.governance.entity.Mandate.builder().member(director).role(com.lias.lias_backend.governance.entity.Mandate.MandateRole.DIRECTOR).startDate(LocalDate.now()).build());
    }

    private void seedUser(
            String email,
            String firstName,
            String lastName,
            User.UserRole role,
            Member.MemberStatus memberStatus,
            String team
    ) {
        if (userRepository.existsByEmail(email)) {
            return;
        }

        User user = userRepository.save(User.builder()
                .email(email)
                .password(passwordEncoder.encode(seedPassword))
                .role(role)
                .status(User.UserStatus.ACTIVE)
                .build());

        Member member = memberRepository.save(Member.builder()
                .user(user)
                .firstName(firstName)
                .lastName(lastName)
                .status(memberStatus)
                .hireDate(LocalDate.now())
                .establishment("Universite Hassan II Casablanca")
                .originLaboratory("LIAS")
                .interests("Laboratory information system")
                .build());

        affiliationRepository.save(Affiliation.builder()
                .member(member)
                .laboratory("LIAS")
                .team(team)
                .startDate(LocalDate.now())
                .build());
    }
}
