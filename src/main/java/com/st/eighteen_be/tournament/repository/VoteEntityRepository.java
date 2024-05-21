package com.st.eighteen_be.tournament.repository;

import com.st.eighteen_be.tournament.domain.entity.VoteEntity;
import com.st.eighteen_be.tournament.repository.querydsl.VoteRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VoteEntityRepository extends JpaRepository<VoteEntity, Long>, VoteRepositoryCustom {

}