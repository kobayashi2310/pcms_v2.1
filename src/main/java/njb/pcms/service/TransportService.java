package njb.pcms.service;

import lombok.RequiredArgsConstructor;
import njb.pcms.dto.pcms.admin.TransportRequestDto;
import njb.pcms.model.Pc;
import njb.pcms.model.Transport;
import njb.pcms.model.User;
import njb.pcms.repository.PcRepository;
import njb.pcms.repository.TransportRepository;
import njb.pcms.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@Transactional
@RequiredArgsConstructor
public class TransportService {

    private final TransportRepository transportRepository;
    private final PcRepository pcRepository;
    private final UserRepository userRepository;

    public List<Transport> getActiveTransports() {
        return transportRepository.findByStatus(Transport.TransportStatus.IN_PROGRESS);
    }

    public List<Transport> getAllTransportHistory() {
        return transportRepository.findAllByOrderByCreatedAtDesc();
    }

    public void createTransport(TransportRequestDto dto) {
        Pc pc = pcRepository.findById(Objects.requireNonNull(dto.getPcId()))
                .orElseThrow(() -> new IllegalArgumentException("PCが見つかりません"));
        User user = userRepository.findById(Objects.requireNonNull(dto.getUserId()))
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        boolean isAlreadyTransported = transportRepository.existsByPc_IdAndStatus(pc.getId(),
                Transport.TransportStatus.IN_PROGRESS);
        if (isAlreadyTransported) {
            throw new IllegalArgumentException("このPCは既に持ち出し中です");
        }

        Transport transport = new Transport();
        transport.setPc(pc);
        transport.setUser(user);
        transport.setDestination(dto.getDestination());
        transport.setReason(dto.getReason());
        transport.setExpectedReturnDate(dto.getExpectedReturnDate());
        transport.setStatus(Transport.TransportStatus.IN_PROGRESS);

        transportRepository.save(transport);
    }

    public void completeTransport(Long id) {
        Transport transport = transportRepository.findById(Objects.requireNonNull(id))
                .orElseThrow(() -> new IllegalArgumentException("該当の持ち出し記録が見つかりません"));

        if (transport.getStatus() == Transport.TransportStatus.COMPLETED) {
            throw new IllegalArgumentException("このPCは既に返却済みとしてマークされています");
        }

        transport.setStatus(Transport.TransportStatus.COMPLETED);
        transport.setReturnedAt(LocalDateTime.now());
        transportRepository.save(transport);
    }

    public java.util.Set<Long> getTransportedPcIds() {
        return transportRepository.findByStatus(Transport.TransportStatus.IN_PROGRESS)
                .stream()
                .map(transport -> transport.getPc().getId())
                .collect(java.util.stream.Collectors.toSet());
    }

}
