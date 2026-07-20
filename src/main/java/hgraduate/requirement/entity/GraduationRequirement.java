package hgraduate.requirement.entity;

import hgraduate.curriculum.entity.CurriculumVersion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "graduation_requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraduationRequirement {

    @Id
    @Column(name = "grad_requirement_id")
    private Long gradRequirementId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_version_id", nullable = false)
    private CurriculumVersion curriculumVersion;

    @Enumerated(EnumType.STRING)
    @Column(name = "requirement_type", nullable = false)
    private RequirementValueType requirementType;

    @Column(name = "required_value", nullable = false)
    private Integer requiredValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "unit", nullable = false)
    private RequirementUnit unit;

    @Column(name = "priority")
    private Integer priority;
}
