package njb.pcms.repository;

import njb.pcms.model.Period;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PeriodRepository extends JpaRepository<Period, Byte> {
}
