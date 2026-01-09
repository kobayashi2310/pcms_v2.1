package njb.pcms.controller.api;

import lombok.RequiredArgsConstructor;
import njb.pcms.dto.pcms.admin.AdminUserResponseDto;
import njb.pcms.model.User;
import njb.pcms.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
public class AdminUserRestController {

    private final UserService userService;

    @GetMapping
    public List<AdminUserResponseDto> getUsers(@RequestParam(name = "year", required = false) Integer year) {
        int targetYear;
        if (year != null) {
            targetYear = year;
        } else {
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();
            targetYear = (currentMonth >= 4) ? currentYear : currentYear - 1;
        }

        String prefix = "T" + (targetYear % 100);
        List<User> students = userService.findByRoleAndStudentIdStartingWith(User.UserRole.STUDENT, prefix);

        return students.stream()
                .map(AdminUserResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
