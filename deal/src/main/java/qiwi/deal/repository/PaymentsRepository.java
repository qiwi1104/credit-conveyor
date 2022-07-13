package qiwi.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qiwi.deal.entity.PaymentScheduleElement;

@Repository
public interface PaymentsRepository extends JpaRepository<PaymentScheduleElement, Long> {
}
