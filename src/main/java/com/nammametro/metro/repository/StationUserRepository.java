package com.nammametro.metro.repository;

import com.nammametro.metro.model.StationUser;
import com.nammametro.metro.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * StationUserRepository - Data access for StationUser entity
 * SOLID: Dependency Inversion - abstract repository pattern
 */
@Repository
public interface StationUserRepository extends JpaRepository<StationUser, Long> {

    Optional<StationUser> findByUserId(Long userId);

    Optional<StationUser> findByStation(Station station);

    List<StationUser> findByOnDutyTrue();

    List<StationUser> findByDepartment(String department);
}
