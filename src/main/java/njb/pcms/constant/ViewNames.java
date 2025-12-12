package njb.pcms.constant;

/**
 * ビュー名の定数
 */
public class ViewNames {

    /**
     * 予約一覧
     */
    public static final String PCMS_RESERVATION = "pcms/reservation";

    /**
     * 自分の予約一覧
     */
    public static final String PCMS_MY_RESERVATIONS = "pcms/myReservations";

    /**
     * 管理者ダッシュボード
     */
    public static final String PCMS_ADMIN_RESERVATIONS = "pcms/admin/admin-dashboard";

    /**
     * 管理者持ち出し管理
     */
    public static final String PCMS_ADMIN_TRANSPORT = "pcms/admin/admin-transport";

    /**
     * 管理者持ち出し履歴
     */
    public static final String PCMS_ADMIN_TRANSPORT_HISTORY = "pcms/admin/admin-transport-history";

    /**
     * 管理者 日別予約一覧
     */
    public static final String PCMS_ADMIN_DAILY_RESERVATIONS = "pcms/admin/daily-reservations";

    /**
     * ログイン
     */
    public static final String PCMS_LOGIN = "pcms/public/login";

    /**
     * ホーム
     */
    public static final String PCMS_HOME = "pcms/home";

    /**
     * 予約一覧
     */
    public static final String REDIRECT_PCMS_RESERVATIONS = "redirect:/pcms/reservations";

    /**
     * 自分の予約一覧
     */
    public static final String REDIRECT_PCMS_MY_RESERVATIONS = "redirect:/pcms/reservations/my-reservations";

    /**
     * 管理者ダッシュボード
     */
    public static final String REDIRECT_PCMS_ADMIN_RESERVATIONS = "redirect:/pcms/admin/dashboard";

    /**
     * 管理者持ち出し管理
     */
    public static final String REDIRECT_PCMS_ADMIN_TRANSPORT = "redirect:/pcms/admin/transport";

    private ViewNames() {
        // Private constructor
    }

}
