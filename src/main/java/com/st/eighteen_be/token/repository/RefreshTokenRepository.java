package com.st.eighteen_be.token.repository;

import com.st.eighteen_be.token.domain.RefreshToken;
import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {
}
