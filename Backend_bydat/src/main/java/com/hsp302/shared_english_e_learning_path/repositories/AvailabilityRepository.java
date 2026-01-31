package com.hsp302.shared_english_e_learning_path.repositories;

import com.hsp302.shared_english_e_learning_path.domain.entities.Availability;
import com.hsp302.shared_english_e_learning_path.domain.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface AvailabilityRepository extends JpaRepository<Availability, UUID> {
    List<Availability> findByConsultantUsernameAndAvailabilityDateTimeIn(String username, List<Instant> availabilityDateTimes);

    List<Availability> findByConsultantUsernameAndAvailabilityDateTimeBetween(String username, Instant fromInstantUtc, Instant toInstantUtc);

    Availability findByConsultantUsernameAndAvailabilityDateTime(String username, Instant time);
}
