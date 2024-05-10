package com.st.eighteen_be.user.repository;

import com.st.eighteen_be.user.domain.UserPrivacy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserPrivacy,Integer> {

    Optional<UserPrivacy> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
}
