package njb.pcms.constant;

import java.util.Set;

public class PeriodConstants {
    public static final byte PERIOD_1 = 1;
    public static final byte PERIOD_2 = 2;
    public static final byte PERIOD_3 = 3;
    public static final byte PERIOD_4 = 4;

    public static final Set<Byte> ALL_PERIODS = Set.of(PERIOD_1, PERIOD_2, PERIOD_3, PERIOD_4);

    private PeriodConstants() {
        // Private constructor to prevent instantiation
    }
}
