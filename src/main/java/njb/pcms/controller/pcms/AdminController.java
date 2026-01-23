package njb.pcms.controller.pcms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import njb.pcms.constant.FlashMessages;
import njb.pcms.constant.UrlPaths;
import njb.pcms.constant.ViewNames;
import njb.pcms.dto.pcms.admin.TransportRequestDto;
import njb.pcms.dto.pcms.admin.TransportUpdateRequestDto;
import njb.pcms.model.User;
import njb.pcms.service.PcService;
import njb.pcms.service.ReservationService;
import njb.pcms.service.TransportService;
import njb.pcms.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
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
        model.addAttribute("activeTransports", transportService.getActiveTransports());
        model.addAttribute("returnReports", reservationService.getGroupedRecentReturnReports());
        model.addAttribute("todayReservationCount",
                reservationService.getReservationCountForDate(LocalDate.now()));
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

    // GET /pcms/admin/transport/history
    @GetMapping("/transport/history")
    public String showTransportHistoryPage(Model model) {
        model.addAttribute("transports", transportService.getAllTransportHistory());
        return ViewNames.PCMS_ADMIN_TRANSPORT_HISTORY;
    }

    // GET /pcms/admin/reservations
    @GetMapping("/reservations")
    public String dailyReservations(
            @RequestParam(name = "date", required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate date,
            Model model) {
        java.time.LocalDate selectedDate = (date == null) ? java.time.LocalDate.now() : date;

        model.addAttribute("reservations", reservationService.getGroupedReservationsByDate(selectedDate));
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("prevDate", selectedDate.minusDays(1));
        model.addAttribute("nextDate", selectedDate.plusDays(1));

        return ViewNames.PCMS_ADMIN_DAILY_RESERVATIONS;
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
            return ViewNames.REDIRECT_PCMS_ADMIN_TRANSPORT;
        }
        try {
            transportService.createTransport(dto);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, "PC持ち出しを登録しました。");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR, "登録処理に失敗しました: " + e.getMessage());
            e.printStackTrace();
        }
        return ViewNames.REDIRECT_PCMS_ADMIN_TRANSPORT;
    }

    // POST /pcms/admin/transport/update
    @PostMapping("/transport/update")
    public String updateTransport(@Validated @ModelAttribute TransportUpdateRequestDto dto, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "入力内容に不備があります。");
            return ViewNames.REDIRECT_PCMS_ADMIN_TRANSPORT;
        }

        try {
            transportService.updateTransport(dto);
            redirectAttributes.addFlashAttribute("successMessage", "返却予定日を更新しました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "予期せぬエラーが発生しました: " + e.getMessage());
            e.printStackTrace();
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
