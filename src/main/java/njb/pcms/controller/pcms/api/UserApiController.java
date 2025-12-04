package njb.pcms.controller.pcms.api;

import lombok.RequiredArgsConstructor;
import njb.pcms.dto.pcms.admin.UserDto;

import njb.pcms.service.UserService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserApiController {

    private final UserService userService;

    // GET /api/users/search
    @GetMapping("/search")
    public List<UserDto> searchUsers(@RequestParam("query") @NotBlank String query) {
        return userService.searchUsers(query)
                .stream()
                .map(UserDto::new)
                .toList();
    }

}
