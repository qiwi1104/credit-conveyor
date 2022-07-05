package qiwi.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qiwi.deal.entity.LoanOffer;

@Repository
public interface LoanOffersRepository extends JpaRepository<LoanOffer, Long> {
}
