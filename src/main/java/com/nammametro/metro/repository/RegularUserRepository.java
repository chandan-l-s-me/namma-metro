package com.nammametro.metro.repository;

import com.nammametro.metro.model.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RegularUserRepository - Data access for RegularUser entity
 * SOLID: Dependency Inversion - abstract repository pattern
 */
@Repository
public interface RegularUserRepository extends JpaRepository<RegularUser, Long> {

    Optional<RegularUser> findByUserId(Long userId);

    List<RegularUser> findByLoyaltyPointsGreaterThan(Integer minPoints);
}
