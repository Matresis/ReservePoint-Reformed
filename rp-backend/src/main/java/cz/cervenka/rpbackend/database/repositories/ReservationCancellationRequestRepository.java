package cz.cervenka.rpbackend.database.repositories;

import cz.cervenka.rpbackend.database.entities.ReservationCancellationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationCancellationRequestRepository extends JpaRepository<ReservationCancellationRequestEntity, Long> {
}
