package njb.pcms.constant;

public class FlashMessages {
    public static final String KEY_SUCCESS = "successMessage";
    public static final String KEY_ERROR = "errorMessage";
    public static final String KEY_BINDING_RESULT_PREFIX = "org.springframework.validation.BindingResult.";

    public static final String MSG_RESERVATION_COMPLETED = "予約申請が完了しました。";
    public static final String MSG_RETURN_REPORT_COMPLETED = "返却報告をしました。";
    public static final String MSG_RETURN_REPORT_FAILED = "返却報告に失敗しました: ";
    public static final String MSG_RETURN_REPORT_REQUIRED = "作業報告は必須です。";
    public static final String MSG_RESERVATION_CANCELLED = "予約をキャンセルしました。";
    public static final String MSG_RESERVATION_CANCEL_FAILED = "予約のキャンセルに失敗しました: ";
    public static final String MSG_RESERVATION_APPROVED = "予約を承認しました。";
    public static final String MSG_RESERVATION_DENIED = "予約を否認しました。";
    public static final String MSG_TRANSPORT_COMPLETED = "搬送完了処理を行いました。";

    private FlashMessages() {
        // Private constructor
    }
}
