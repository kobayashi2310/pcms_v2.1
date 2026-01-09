package njb.pcms.controller.pcms;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import njb.pcms.dto.BulkStudentRegistrationDto;
import njb.pcms.dto.StudentRegistrationPreviewDto;
import njb.pcms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/pcms/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    @GetMapping
    public String listUsers(
            @RequestParam(name = "year", required = false) Integer year,
            Model model) {
        int targetYear;
        if (year != null) {
            targetYear = year;
        } else {
            // 現在の年を取得
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();
            targetYear = (currentMonth >= 4) ? currentYear : currentYear - 1;
        }

        model.addAttribute("selectedYear", targetYear);
        return "pcms/admin/user/list";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("bulkStudentRegistrationDto")) {
            BulkStudentRegistrationDto dto = new BulkStudentRegistrationDto();
            LocalDate now = LocalDate.now();
            int currentYear = now.getYear();
            int currentMonth = now.getMonthValue();
            dto.setAdmissionYear((currentMonth >= 4) ? currentYear : currentYear - 1);
            model.addAttribute("bulkStudentRegistrationDto", dto);
        }
        return "pcms/admin/user/create";
    }

    @PostMapping("/confirm")
    public String confirmStudents(@Valid @ModelAttribute BulkStudentRegistrationDto dto,
            BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return "pcms/admin/user/create";
        }

        StudentRegistrationPreviewDto preview = userService.previewStudentRegistration(dto);

        if (preview.hasErrors()) {
            model.addAttribute("validationErrors", preview.getErrorMessages());
            return "pcms/admin/user/create";
        }

        model.addAttribute("preview", preview);
        model.addAttribute("bulkStudentRegistrationDto", dto);
        return "pcms/admin/user/confirm";
    }

    @PostMapping("/create")
    public String createStudents(@Valid @ModelAttribute BulkStudentRegistrationDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "pcms/admin/user/create";
        }

        try {
            userService.registerStudentsBulk(dto);
            redirectAttributes.addFlashAttribute("successMessage", "学生を一括登録しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "登録に失敗しました: " + e.getMessage());
            return "redirect:/pcms/admin/users/create";
        }

        return "redirect:/pcms/admin/users?year=" + dto.getAdmissionYear();
    }
}
