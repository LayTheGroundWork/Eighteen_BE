package com.st.eighteen_be.member.repository;

import com.st.eighteen_be.member.domain.MemberPrivacy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberPrivacy,Integer> {

    Optional<MemberPrivacy> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
}
