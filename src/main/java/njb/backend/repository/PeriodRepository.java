package njb.backend.repository;

import njb.backend.model.Period;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository extends JpaRepository<Period, Byte> {
}
