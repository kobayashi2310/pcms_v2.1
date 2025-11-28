package njb.pcms.controller.pcms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import njb.pcms.dto.pcms.admin.TransportRequestDto;
import njb.pcms.model.User;
import njb.pcms.repository.PcRepository;
import njb.pcms.repository.UserRepository;
import njb.pcms.service.ReservationService;
import njb.pcms.service.TransportService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/pcms/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ReservationService reservationService;
    private final PcRepository pcRepository;
    private final UserRepository userRepository;
    private final TransportService transportService;

    // GET /pcms/admin
    @GetMapping
    public String adminRoot() {
        return "redirect:/pcms/admin/dashboard";
    }

    // GET /pcms/admin/dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pendingReservations", reservationService.getPendingReservations());
        return "pcms/admin/admin-dashboard";
    }

    // POST /pcms/admin/reservations/approve
    @PostMapping("/reservations/approve")
    public String approveReservation(
            @RequestParam("reservationIds")
            String reservationIdsStr,
            RedirectAttributes redirectAttributes
    ) {
        try {
            List<Long> reservationIds = parseReservationIds(reservationIdsStr);
            reservationService.approveReservations(reservationIds);
            redirectAttributes.addFlashAttribute("successMessage", "予約を承認しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "承認処理に失敗しました: " + e.getMessage());
        }
        return "redirect:/pcms/admin/dashboard";
    }

    // POST /pcms/admin/reservations/deny
    @PostMapping("/reservations/deny")
    public String denyReservation(
            @RequestParam("reservationIds")
            String reservationIdsStr,
            RedirectAttributes redirectAttributes
    ) {
        try {
            List<Long> reservationIds = parseReservationIds(reservationIdsStr);
            reservationService.denyReservations(reservationIds);
            redirectAttributes.addFlashAttribute("successMessage", "予約を否認しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "否認処理に失敗しました: " + e.getMessage());
        }
        return "redirect:/pcms/admin/dashboard";
    }

    private List<Long> parseReservationIds(String reservationIdsStr) {
        if (StringUtils.hasText(reservationIdsStr)) {
            return Arrays.stream(reservationIdsStr.split(","))
                    .map(Long::parseLong)
                    .toList();
        }
        return Collections.emptyList();
    }

    // GET /pcms/admin/transport
    @GetMapping("/transport")
    public String showTransportPage(Model model) {
        model.addAttribute("newTransport", new TransportRequestDto());
        model.addAttribute("pcs", pcRepository.findAll());
        model.addAttribute("users", userRepository.findByRole(User.UserRole.STUDENT));
        model.addAttribute("activeTransports", transportService.getActiveTransports());
        return "pcms/admin/admin-transport";
    }


    // TODO 持ち出し開始日、終了時の設定をする。ユーザー検索機能の追加

    // GET /pcms/admin/transport/history
    @GetMapping("/transport/history")
    public String showTransportHistoryPage(Model model) {
        model.addAttribute("transports", transportService.getAllTransportHistory());
        return "pcms/admin/admin-transport-history";
    }

    // POST /pcms/admin/transport
    @PostMapping("/transport")
    public String createTransport(
            @Valid @ModelAttribute("newTransport") TransportRequestDto dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes)
    {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.newTransport", bindingResult);
            redirectAttributes.addFlashAttribute("newTransport", dto);
        }
        try {
            transportService.createTransport(dto);
            redirectAttributes.addFlashAttribute("successMessage", "PC持ち出しを登録しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "処理に失敗しました: " + e.getMessage());
        }
        return "redirect:/pcms/admin/transport";
    }

    // POST /transport/complete/{id}
    @PostMapping("/transport/complete/{id}")
    public String completeTransport(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            transportService.completeTransport(id);
            redirectAttributes.addFlashAttribute("successMessage", "PCを返却済みにしました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "処理に失敗しました: " + e.getMessage());
        }
        return "redirect:/pcms/admin/transport";
    }

}
