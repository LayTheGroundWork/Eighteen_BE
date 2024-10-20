package com.st.eighteen_be.config.repository;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.config.BootstrapMode;

@Configuration
@EnableJpaRepositories(basePackages = {
        "com.st.eighteen_be.tournament.repository",
        "com.st.eighteen_be.user.repository",
        "com.st.eighteen_be.tournament_winner.repository"
}, bootstrapMode = BootstrapMode.DEFERRED
)
public class JpaConfig {
}
