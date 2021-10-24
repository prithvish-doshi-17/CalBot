package com.se21.calbot.repositories;

import com.se21.calbot.model.AuthToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository extends JpaRepository class to provide all basic CRUD operations of JPA library.
 * All db operations are managed using TokensRepository object in aPAS
 */
@Repository
public interface TokensRepository extends JpaRepository<AuthToken, String> {
}
