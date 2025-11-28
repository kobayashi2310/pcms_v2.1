package njb.pcms.service;

import lombok.RequiredArgsConstructor;
import njb.pcms.model.Pc;
import njb.pcms.repository.PcRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PcService {

    private final PcRepository pcRepository;

    public List<Pc> findAll() {
        return pcRepository.findAll();
    }

}
