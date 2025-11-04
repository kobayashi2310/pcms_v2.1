package njb.pcms.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "transport")
@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class Transport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "pc_id", nullable = false)
    private Pc pc;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'IN_PROGRESS'")
    @Enumerated(EnumType.STRING)
    private TransportStatus status;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "expected_return_date")
    private LocalDate expectedReturnDate;

    @Column(name = "returned_at")
    private LocalDateTime returnedAt;

    public enum TransportStatus {
        /**
         * PCはこの期間「予約不可」扱い
         */
        IN_PROGRESS,
        /**
         * 返却済み・完了
         */
        COMPLETED
    }

}
