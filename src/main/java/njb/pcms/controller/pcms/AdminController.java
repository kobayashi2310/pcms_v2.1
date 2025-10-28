package njb.pcms.controller.pcms;

import lombok.RequiredArgsConstructor;
import njb.pcms.service.ReservationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/pcms/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReservationService reservationService;

    @GetMapping
    public String adminRoot() {
        return "redirect:/pcms/admin/dashboard";
    }

    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pendingReservations", reservationService.getPendingReservations());
        return "pcms/admin-dashboard";
    }

    @PostMapping("/reservations/approve/{id}")
    public String approveReservation(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            reservationService.approveReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "予約ID: " + id + " を承認しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "承認処理に失敗しました: " + e.getMessage());
        }
        return "redirect:/pcms/admin/dashboard";
    }

    @PostMapping("/reservations/deny/{id}")
    public String denyReservation(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            reservationService.denyReservation(id);
            redirectAttributes.addFlashAttribute("successMessage", "予約ID: " + id + " を否認しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "否認処理に失敗しました: " + e.getMessage());
        }
        return "redirect:/pcms/admin/dashboard";
    }


}
