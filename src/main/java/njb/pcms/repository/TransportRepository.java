package njb.pcms.repository;

import njb.pcms.model.Transport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportRepository extends JpaRepository<Transport, Long> {

    List<Transport> findByStatus(Transport.TransportStatus status);

    boolean existsByPc_IdAndStatus(Long pcId, Transport.TransportStatus status);

}
