package njb.pcms.constant;

/**
 * URLパスの定数
 */
public class UrlPaths {

    /**
     * 予約一覧
     */
    public static final String PCMS_RESERVATIONS = "/pcms/reservations";

    /**
     * 自分の予約一覧
     */
    public static final String PCMS_MY_RESERVATIONS = "/pcms/reservations/my-reservations";

    /**
     * 管理者
     */
    public static final String PCMS_ADMIN = "/pcms/admin";

    /**
     * 管理者ダッシュボード
     */
    public static final String PCMS_ADMIN_RESERVATIONS = "/pcms/admin/dashboard";

    /**
     * 管理者持ち出し管理
     */
    public static final String PCMS_ADMIN_TRANSPORT = "/pcms/admin/transport";

    /**
     * ホーム
     */
    public static final String PCMS_HOME = "/pcms";

    private UrlPaths() {
        // Private constructor
    }
}
