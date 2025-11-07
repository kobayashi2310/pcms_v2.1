package njb.pcms.controller.pcms.api;

import lombok.RequiredArgsConstructor;
import njb.pcms.dto.pcms.admin.UserDto;
import njb.pcms.repository.UserRepository;
import njb.pcms.util.KanaConverter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserApiController {

    private final UserRepository userRepository;
    private final KanaConverter kanaConverter;

    @GetMapping("/search")
    public List<UserDto> searchUsers(@RequestParam("query") String query) {
        if (query.length() < 2) {
            return List.of();
        }

        String  katakana = kanaConverter.hiraganaToKatakana(query);

        return userRepository.findTop10ByStudentIdContainingOrNameContainingOrKanaContaining(query, query, katakana)
                .stream()
                .map(UserDto::new)
                .toList();
    }

}
