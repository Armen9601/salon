package com.gevorgyan.salon.repo;

import com.gevorgyan.salon.domain.BusyTime;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BusyTimeRepository extends JpaRepository<BusyTime, Long> {

  @Query("""
      select bt from BusyTime bt
      where bt.barber.id = :barberId
        and bt.endAt > :from
        and bt.startAt < :to
      order by bt.startAt asc
      """)
  List<BusyTime> findOverlappingBusyTimes(
      @Param("barberId") Long barberId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  @Override
  @EntityGraph(attributePaths = {"barber"})
  List<BusyTime> findAll();
}

