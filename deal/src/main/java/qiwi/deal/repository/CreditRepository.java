package qiwi.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qiwi.deal.entity.Credit;

@Repository
public interface CreditRepository extends JpaRepository<Credit, Long> {
}
