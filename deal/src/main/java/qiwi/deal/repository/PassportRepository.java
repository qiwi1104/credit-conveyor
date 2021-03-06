package qiwi.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qiwi.deal.entity.Passport;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
}
