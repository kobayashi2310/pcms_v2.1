package njb.backend.controller.pcms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import njb.backend.dto.pcms.reservation.ReservationRequestDto;
import njb.backend.repository.PcRepository;
import njb.backend.repository.PeriodRepository;
import njb.backend.service.ReservationService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
@RequestMapping("/pcms/reservations")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PcRepository pcRepository;
    private final PeriodRepository periodRepository;

    @GetMapping
    public String reservationPage(@RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date, Model model) {
        LocalDate selectedDate = (date == null) ? LocalDate.now() : date;

        model.addAttribute("reservations", reservationService.getReservationsByDate(selectedDate));

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
}
