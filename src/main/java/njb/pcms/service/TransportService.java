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

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class TransportService {

    private final TransportRepository transportRepository;
    private final PcRepository pcRepository;
    private UserRepository userRepository;

    public List<Transport> getActiveTransports() {
        return transportRepository.findByStatus(Transport.TransportStatus.IN_PROGRESS);
    }

    public void createTransport(TransportRequestDto dto) {
        Pc pc = pcRepository.findById(dto.getPcId())
                .orElseThrow(() -> new IllegalArgumentException("PCが見つかりません"));
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません"));

        boolean isAlreadyTransported = transportRepository.existsByPc_IdAndStatus(pc.getId(), Transport.TransportStatus.IN_PROGRESS);
        if (isAlreadyTransported) {
            throw new IllegalArgumentException("このPCは既に持ち出し中です");
        }

        Transport transport = new Transport();
        transport.setPc(pc);
        transport.setUser(user);
        transport.setDestination(dto.getDestination());
        transport.setReason(dto.getReason());
        transport.setStatus(Transport.TransportStatus.IN_PROGRESS);

        transportRepository.save(transport);
    }

}
