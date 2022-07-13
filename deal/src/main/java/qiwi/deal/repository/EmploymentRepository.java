package qiwi.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qiwi.deal.entity.Employment;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, Long> {
}
