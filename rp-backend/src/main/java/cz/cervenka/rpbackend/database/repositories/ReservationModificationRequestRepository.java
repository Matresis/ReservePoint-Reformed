package cz.cervenka.rpbackend.database.repositories;

import cz.cervenka.rpbackend.database.entities.ReservationModificationRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationModificationRequestRepository extends JpaRepository<ReservationModificationRequestEntity, Long> {
}
