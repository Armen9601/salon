package com.gevorgyan.salon.repo;

import com.gevorgyan.salon.domain.Availability;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AvailabilityRepository extends JpaRepository<Availability, Long> {

  @Query("""
      select a from Availability a
      where a.barber.id = :barberId
        and a.endAt > :from
        and a.startAt < :to
      order by a.startAt asc
      """)
  List<Availability> findOverlappingRanges(
      @Param("barberId") Long barberId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  @Override
  @EntityGraph(attributePaths = {"barber"})
  List<Availability> findAll();
}

