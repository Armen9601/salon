package com.gevorgyan.salon.repo;

import com.gevorgyan.salon.domain.Booking;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BookingRepository extends JpaRepository<Booking, Long> {

  @Query("""
      select b from Booking b
      where b.barber.id = :barberId
        and b.endAt > :from
        and b.startAt < :to
      order by b.startAt asc
      """)
  List<Booking> findOverlappingBookings(
      @Param("barberId") Long barberId,
      @Param("from") LocalDateTime from,
      @Param("to") LocalDateTime to);

  @Query("""
      select b from Booking b
      order by b.startAt desc
      """)
  @EntityGraph(attributePaths = {"barber", "services"})
  List<Booking> findAllOrderByStartDesc();
}

