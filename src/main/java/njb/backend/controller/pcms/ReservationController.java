package njb.backend.controller.pcms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import njb.backend.dto.pcms.reservation.ReservationRequestDto;
import njb.backend.dto.pcms.returned.ReturnReportDto;
import njb.backend.repository.PcRepository;
import njb.backend.repository.PeriodRepository;
import njb.backend.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping("/pcms/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PcRepository pcRepository;
    private final PeriodRepository periodRepository;

    @GetMapping
    public String reservationPage(
            @RequestParam(name = "date", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            Model model
    ) {
        LocalDate selectedDate = (date == null) ? LocalDate.now() : date;

        model.addAttribute("reservations", reservationService.getReservationsByDate(selectedDate));

        Map<Long, Set<Byte>> bookedPcsAndPeriods = reservationService.getBookedPcsAndPeriodsForDate(selectedDate);
        model.addAttribute("bookedPcsAndPeriods", bookedPcsAndPeriods);

        if (!model.containsAttribute("reservationRequest")) {
            ReservationRequestDto dto = new ReservationRequestDto();
            dto.setDate(selectedDate);
            model.addAttribute("reservationRequest", dto);
        }

        model.addAttribute("pcs", pcRepository.findAll());
        model.addAttribute("periods", periodRepository.findAll());
        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("prevDate", selectedDate.minusDays(1));
        model.addAttribute("nextDate", selectedDate.plusDays(1));

        return "pcms/reservation";
    }

    @PostMapping
    public String createReservation(@Valid @ModelAttribute("reservationRequest") ReservationRequestDto reservationRequest,
                                    BindingResult bindingResult,
                                    Authentication authentication,
                                    RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reservationRequest", bindingResult);
            redirectAttributes.addFlashAttribute("reservationRequest", reservationRequest);
            return "redirect:/pcms/reservations?date=" + reservationRequest.getDate();
        }

        try {
            String studentId = authentication.getName();
            reservationService.createReservation(reservationRequest, studentId);
            redirectAttributes.addFlashAttribute("successMessage", "予約申請が完了しました。");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }

        return "redirect:/pcms/reservations?date=" + reservationRequest.getDate();
    }

    @GetMapping("/my-reservations")
    public String myReservationPage(
            Authentication authentication,
            Model model
    ) {
        String studentId = authentication.getName();
        model.addAttribute("myReservations", reservationService.findGroupedReservationsByStudentId(studentId));
        return "pcms/my-reservations";
    }

    @PostMapping("/report-return/{id}")
    public String reportReturn(
            @PathVariable("id") Long reservationId,
            @ModelAttribute ReturnReportDto dto,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            String studentId = authentication.getName();
            reservationService.reportReturnByStudent(reservationId, studentId, dto);
            redirectAttributes.addFlashAttribute(
                    "successMessage",
                    "予約ID:" + reservationId + "の返却報告を行いました"
            );
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    "返却報告に失敗しました:" + e.getMessage()
            );
        }
        return "redirect:/pcms/reservations/my-reservations";
    }

    @PostMapping("/cancel")
    public String cancelReservations(
            @RequestParam("reservationIds") String reservationIdsStr,
            Authentication authentication,
            RedirectAttributes redirectAttributes
    ) {
        try {
            List<Long> reservationIds;
            if (StringUtils.hasText(reservationIdsStr)) {
                reservationIds = Arrays.stream(reservationIdsStr.split(","))
                        .map(Long::parseLong)
                        .toList();
            } else {
                reservationIds = Collections.emptyList();
            }

            String studentId = authentication.getName();
            reservationService.cancelReservationsByStudent(reservationIds, studentId);
            redirectAttributes.addFlashAttribute("successMessage", "予約をキャンセルしました");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "予約のキャンセルに失敗しました:" + e.getMessage());
        }
        return "redirect:/pcms/reservations/my-reservations";
    }

}
