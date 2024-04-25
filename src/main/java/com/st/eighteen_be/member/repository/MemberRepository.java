package com.st.eighteen_be.member.repository;

import com.st.eighteen_be.member.domain.MemberPrivacy;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberPrivacy,Integer> {

    boolean existsByPhoneNumber(String phoneNumber);
}
