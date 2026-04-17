package com.nammametro.metro.repository;

import com.nammametro.metro.model.Train;
import com.nammametro.metro.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRepository extends JpaRepository<Train, Long> {

    List<Train> findByRoute(Route route);
}