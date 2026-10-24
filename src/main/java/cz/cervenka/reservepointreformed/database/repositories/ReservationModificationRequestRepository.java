package cz.cervenka.reservepointreformed.database.repositories;

import cz.cervenka.reservepointreformed.database.entities.ReservationModificationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationModificationRequestRepository extends JpaRepository<ReservationModificationRequestEntity, Long> {
}
