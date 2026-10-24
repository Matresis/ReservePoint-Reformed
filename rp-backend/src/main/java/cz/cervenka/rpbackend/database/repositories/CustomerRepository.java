package cz.cervenka.rpbackend.database.repositories;

import cz.cervenka.rpbackend.database.entities.CustomerEntity;
import cz.cervenka.rpbackend.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByUser(UserEntity user);
}
