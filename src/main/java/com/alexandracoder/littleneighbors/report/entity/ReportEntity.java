package com.alexandracoder.littleneighbors.report.entity;

import com.alexandracoder.littleneighbors.enums.ReportReason;
import com.alexandracoder.littleneighbors.enums.ReportStatus;
import com.alexandracoder.littleneighbors.enums.ReportType;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reports")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class ReportEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "reporter_user_id", nullable = false)
    private UserEntity reporterUser;

    @ManyToOne
    @JoinColumn(name = "reported_user_id")
    private UserEntity reportedUser;

    @ManyToOne
    @JoinColumn(name = "reported_family_id")
    private FamilyEntity reportedFamily;

    @Enumerated(EnumType.STRING)
    @Column(name = "report_type", nullable = false, length = 20)
    private ReportType reportType;

    // ID del mensaje, familia o evento reportado, según reportType.
    @Column(name = "related_id")
    private Long relatedId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private ReportReason reason;

    @Column(length = 1000)
    private String description;

    // Copia del contenido en el momento del reporte (p.ej. el texto del
    // mensaje), para que quede constancia aunque el contenido cambie o se
    // borre después.
    @Column(name = "content_snapshot", length = 2000)
    private String contentSnapshot;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.PENDING;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @ManyToOne
    @JoinColumn(name = "resolved_by_admin_id")
    private UserEntity resolvedByAdmin;
}
