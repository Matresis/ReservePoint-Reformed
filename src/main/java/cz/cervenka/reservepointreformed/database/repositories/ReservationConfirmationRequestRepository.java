package cz.cervenka.reservepointreformed.database.repositories;

import cz.cervenka.reservepointreformed.database.entities.ReservationConfirmationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservationConfirmationRequestRepository extends JpaRepository<ReservationConfirmationRequestEntity, Long> {
}
