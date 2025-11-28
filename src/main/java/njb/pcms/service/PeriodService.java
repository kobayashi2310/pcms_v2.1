package njb.pcms.service;

import lombok.RequiredArgsConstructor;
import njb.pcms.model.Period;
import njb.pcms.repository.PeriodRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class PeriodService {

    private final PeriodRepository periodRepository;

    public List<Period> findAll() {
        return periodRepository.findAll();
    }
}
