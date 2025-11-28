package njb.pcms.controller.pcms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import njb.pcms.constant.FlashMessages;
import njb.pcms.constant.UrlPaths;
import njb.pcms.constant.ViewNames;
import njb.pcms.dto.pcms.admin.TransportRequestDto;
import njb.pcms.model.User;
import njb.pcms.service.PcService;
import njb.pcms.service.ReservationService;
import njb.pcms.service.TransportService;
import njb.pcms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(UrlPaths.PCMS_ADMIN)
@RequiredArgsConstructor
public class AdminController {

    private final ReservationService reservationService;
    private final PcService pcService;
    private final UserService userService;
    private final TransportService transportService;

    @ModelAttribute("requestURI")
    public String requestURI(HttpServletRequest request) {
        return request.getRequestURI();
    }

    // GET /pcms/admin
    @GetMapping
    public String adminRoot() {
        return ViewNames.REDIRECT_PCMS_ADMIN_RESERVATIONS;
    }

    // GET /pcms/admin/dashboard
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        model.addAttribute("pendingReservations", reservationService.getPendingReservations());
        return ViewNames.PCMS_ADMIN_RESERVATIONS;
    }

    // POST /pcms/admin/reservations/approve
    @PostMapping("/reservations/approve")
    public String approveReservation(
            @RequestParam(name = "reservationIds", required = false) List<Long> reservationIds,
            RedirectAttributes redirectAttributes) {
        try {
            if (reservationIds == null) {
                reservationIds = Collections.emptyList();
            }
            reservationService.approveReservations(reservationIds);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, FlashMessages.MSG_RESERVATION_APPROVED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR, "承認処理に失敗しました: " + e.getMessage());
        }
        return ViewNames.REDIRECT_PCMS_ADMIN_RESERVATIONS;
    }

    // POST /pcms/admin/reservations/deny
    @PostMapping("/reservations/deny")
    public String denyReservation(
            @RequestParam(name = "reservationIds", required = false) List<Long> reservationIds,
            RedirectAttributes redirectAttributes) {
        try {
            if (reservationIds == null) {
                reservationIds = Collections.emptyList();
            }
            reservationService.denyReservations(reservationIds);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, FlashMessages.MSG_RESERVATION_DENIED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR, "否認処理に失敗しました: " + e.getMessage());
        }
        return ViewNames.REDIRECT_PCMS_ADMIN_RESERVATIONS;
    }

    // GET /pcms/admin/transport
    @GetMapping("/transport")
    public String showTransportPage(Model model) {
        model.addAttribute("newTransport", new TransportRequestDto());
        model.addAttribute("pcs", pcService.findAll());
        model.addAttribute("users", userService.findByRole(User.UserRole.STUDENT));
        model.addAttribute("activeTransports", transportService.getActiveTransports());
        return ViewNames.PCMS_ADMIN_TRANSPORT;
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
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_BINDING_RESULT_PREFIX + "newTransport",
                    bindingResult);
            redirectAttributes.addFlashAttribute("newTransport", dto);
        }
        try {
            transportService.createTransport(dto);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, "PC持ち出しを登録しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR, "処理に失敗しました: " + e.getMessage());
        }
        return ViewNames.REDIRECT_PCMS_ADMIN_TRANSPORT;
    }

    // POST /transport/complete/{id}
    @PostMapping("/transport/complete/{id}")
    public String completeTransport(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            transportService.completeTransport(id);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, FlashMessages.MSG_TRANSPORT_COMPLETED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR, "処理に失敗しました: " + e.getMessage());
        }
        return ViewNames.REDIRECT_PCMS_ADMIN_TRANSPORT;
    }

}
