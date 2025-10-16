package njb.pcms.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "reservation")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "pc_id", nullable = false)
    private Pc pc;

    @Column(nullable = false)
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    private Period period;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING_APPROVAL'")
    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "retracted_at")
    private LocalDateTime retractedAt;

    @Column(name = "retraction_reason")
    private String retractionReason;

    @RequiredArgsConstructor
    @Getter
    public enum ReservationStatus {
        /**
         * 承諾目
         */
        PENDING_APPROVAL("承認待ち"),
        /**
         * 承諾後
         */
        APPROVED("承認済み"),
        /**
         * 返却済み
         */
        RETRACTED("返却済み");

        private final String displayName;
    }

}
