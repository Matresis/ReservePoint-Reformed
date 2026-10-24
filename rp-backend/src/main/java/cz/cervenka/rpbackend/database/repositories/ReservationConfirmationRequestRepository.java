package cz.cervenka.rpbackend.database.repositories;

import cz.cervenka.rpbackend.database.entities.ReservationConfirmationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationConfirmationRequestRepository extends JpaRepository<ReservationConfirmationRequestEntity, Long> {
}
