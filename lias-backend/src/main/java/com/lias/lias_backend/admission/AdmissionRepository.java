package com.lias.lias_backend.admission;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AdmissionRepository extends JpaRepository<Admission,Long>{boolean existsByEmailIgnoreCaseAndDecision(String email,String decision);}
