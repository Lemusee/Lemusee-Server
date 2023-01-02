package com.lemusee.lemusee_prj.repository;

import com.lemusee.lemusee_prj.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Integer> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByEmailAndProvider(String email,String provider);

    Optional<Member> findById(Integer userId);

    Boolean existsByEmailAndProvider(String email, String provider);

}
