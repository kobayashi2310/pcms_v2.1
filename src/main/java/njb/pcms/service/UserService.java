package njb.pcms.service;

import lombok.RequiredArgsConstructor;
import njb.pcms.model.User;
import njb.pcms.repository.UserRepository;
import njb.pcms.util.KanaConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<User> findByRole(User.UserRole role) {
        return userRepository.findByRole(role);
    }

    private final KanaConverter kanaConverter;

    public List<User> searchUsers(String query) {
        if (query.length() < 1) {
            return List.of();
        }

        String katakana = kanaConverter.hiraganaToKatakana(query);

        return userRepository.findTop10ByStudentIdContainingOrNameContainingOrKanaContaining(query, query, katakana);
    }

}
