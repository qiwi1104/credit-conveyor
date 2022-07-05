package qiwi.deal.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import qiwi.deal.entity.Application;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
}
