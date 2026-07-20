package hgraduate.rulemapping.entity;

import java.math.BigDecimal;

import hgraduate.course.entity.Course;
import hgraduate.curriculum.entity.CurriculumVersion;
import hgraduate.requirement.entity.Requirement;
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

// rule_id stays a plain String (not a relation to GraduationRule) — a real row's value ("R001") doesn't match the integer PK format (Validation_Report_v1.1.md §3).
@Entity
@Table(name = "course_requirement_mapping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRequirementMapping {

    @Id
    @Column(name = "mapping_id")
    private Long mappingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_version_id", nullable = false)
    private CurriculumVersion curriculumVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    @Column(name = "recognized_credit")
    private BigDecimal recognizedCredit;

    @Column(name = "priority")
    private Integer priority;

    @Enumerated(EnumType.STRING)
    @Column(name = "duplication_policy", nullable = false)
    private MappingDuplicationPolicy duplicationPolicy;

    @Column(name = "rule_id")
    private String ruleId;
}
