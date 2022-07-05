package qiwi.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qiwi.deal.entity.ApplicationStatusHistory;

@Repository
public interface ApplicationsStatusHistoryRepository extends JpaRepository<ApplicationStatusHistory, Long> {
}
