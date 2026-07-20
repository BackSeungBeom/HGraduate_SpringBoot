package hgraduate.curriculum.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// No department_id column — seed-data v1.1 lacks it vs system-design.md §2.3 (Schema Difference, not added here).
@Entity
@Table(name = "curriculum_versions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurriculumVersion {

    @Id
    @Column(name = "curriculum_version_id")
    private Long curriculumVersionId;

    @Column(name = "version", nullable = false)
    private String version;

    @Column(name = "credit_system")
    private Integer creditSystem;

    @Column(name = "effective_from")
    private Integer effectiveFrom;

    @Column(name = "effective_to")
    private Integer effectiveTo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CurriculumStatus status;
}
