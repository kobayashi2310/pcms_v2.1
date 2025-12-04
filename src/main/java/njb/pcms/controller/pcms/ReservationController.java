package njb.pcms.controller.pcms;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import njb.pcms.constant.FlashMessages;
import njb.pcms.constant.UrlPaths;
import njb.pcms.constant.ViewNames;
import njb.pcms.dto.pcms.reservation.ReservationRequestDto;
import njb.pcms.dto.pcms.returned.ReturnReportDto;

import njb.pcms.service.PcService;
import njb.pcms.service.PeriodService;
import njb.pcms.service.ReservationService;
import njb.pcms.service.TransportService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.*;

@Controller
@RequestMapping(UrlPaths.PCMS_RESERVATIONS)
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;
    private final PcService pcService;
    private final PeriodService periodService;
    private final TransportService transportService;

    /**
     * GET /pcms/reservations
     * 予約ページを表示します。
     *
     * @param date  表示する日付 (オプション)。指定がない場合は現在の日付が使用されます。
     * @param model 画面に渡すデータを格納するモデル
     * @return 予約ページのビュー名
     */
    @GetMapping
    public String reservationPage(
            @RequestParam(name = "date", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model) {
        LocalDate selectedDate = (date == null) ? LocalDate.now() : date;

        model.addAttribute("reservations", reservationService.getGroupedReservationsByDate(selectedDate));
        Map<Long, Set<Byte>> bookedPcsAndPeriods = reservationService.getBookedPcsAndPeriodsForDate(selectedDate);
        model.addAttribute("bookedPcsAndPeriods", bookedPcsAndPeriods);

        if (!model.containsAttribute("reservationRequest")) {
            ReservationRequestDto dto = new ReservationRequestDto();
            dto.setDate(selectedDate);
            model.addAttribute("reservationRequest", dto);
        }

        model.addAttribute("pcs", pcService.findAll());
        model.addAttribute("periods", periodService.findAll());

        Set<Long> transportedPcIds = transportService.getTransportedPcIds();
        model.addAttribute("transportedPcIds", transportedPcIds);

        model.addAttribute("selectedDate", selectedDate);
        model.addAttribute("prevDate", selectedDate.minusDays(1));
        model.addAttribute("nextDate", selectedDate.plusDays(1));

        return ViewNames.PCMS_RESERVATION;
    }

    /**
     * POST /pcms/reservations
     * 新しい予約を作成します。
     *
     * @param reservationRequest 予約リクエスト情報
     * @param bindingResult      バリデーション結果
     * @param authentication     認証情報 (ログインユーザーの特定に使用)
     * @param redirectAttributes リダイレクト先にフラッシュ属性を渡すためのオブジェクト
     * @return リダイレクト先のビュー名
     */
    @PostMapping
    public String createReservation(
            @Valid @ModelAttribute("reservationRequest") ReservationRequestDto reservationRequest,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    FlashMessages.KEY_BINDING_RESULT_PREFIX + "reservationRequest",
                    bindingResult);
            redirectAttributes.addFlashAttribute("reservationRequest", reservationRequest);
            return ViewNames.REDIRECT_PCMS_RESERVATIONS + "?date=" + reservationRequest.getDate();
        }

        try {
            String studentId = authentication.getName();
            reservationService.createReservation(reservationRequest, studentId);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, FlashMessages.MSG_RESERVATION_COMPLETED);
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR, e.getMessage());
        }

        return ViewNames.REDIRECT_PCMS_RESERVATIONS + "?date=" + reservationRequest.getDate();
    }

    /**
     * GET /pcms/reservations/my-reservations
     * 自分の予約一覧ページを表示します。
     *
     * @param authentication 認証情報
     * @param model          画面モデル
     * @return マイ予約ページのビュー名
     */
    @GetMapping("/my-reservations")
    public String myReservationsPage(Authentication authentication, Model model) {
        String studentId = authentication.getName();
        model.addAttribute("myReservations", reservationService.findGroupedReservationsByStudentId(studentId));
        return ViewNames.PCMS_MY_RESERVATIONS;
    }

    /**
     * POST /pcms/reservations/report-return
     * 返却報告を行います。
     *
     * @param dto                返却報告データ
     * @param bindingResult      バリデーション結果
     * @param authentication     認証情報
     * @param redirectAttributes リダイレクト属性
     * @return リダイレクト先のビュー名
     */
    @PostMapping("/report-return")
    public String reportReturn(
            @Valid @ModelAttribute ReturnReportDto dto,
            BindingResult bindingResult,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR, FlashMessages.MSG_RETURN_REPORT_REQUIRED);
            return ViewNames.REDIRECT_PCMS_MY_RESERVATIONS;
        }

        try {
            String studentId = authentication.getName();
            reservationService.reportReturnByStudent(dto, studentId);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, FlashMessages.MSG_RETURN_REPORT_COMPLETED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR,
                    FlashMessages.MSG_RETURN_REPORT_FAILED + e.getMessage());
        }
        return ViewNames.REDIRECT_PCMS_MY_RESERVATIONS;
    }

    /**
     * POST /pcms/reservations/cancel
     * 予約をキャンセルします。
     *
     * @param reservationIds     キャンセルする予約IDのリスト
     * @param authentication     認証情報
     * @param redirectAttributes リダイレクト属性
     * @return リダイレクト先のビュー名
     */
    @PostMapping("/cancel")
    public String cancelReservations(
            @RequestParam(name = "reservationIds", required = false) List<Long> reservationIds,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        try {
            if (reservationIds == null) {
                reservationIds = Collections.emptyList();
            }

            String studentId = authentication.getName();
            reservationService.cancelReservationsByStudent(reservationIds, studentId);
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_SUCCESS, FlashMessages.MSG_RESERVATION_CANCELLED);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(FlashMessages.KEY_ERROR,
                    FlashMessages.MSG_RESERVATION_CANCEL_FAILED + e.getMessage());
        }
        return ViewNames.REDIRECT_PCMS_MY_RESERVATIONS;
    }

}
