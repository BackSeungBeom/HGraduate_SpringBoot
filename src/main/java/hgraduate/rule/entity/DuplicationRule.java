package hgraduate.rule.entity;

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

@Entity
@Table(name = "duplicate_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DuplicationRule {

    @Id
    @Column(name = "duplicate_id")
    private Long duplicateId;

    @Column(name = "group_id", nullable = false)
    private Integer groupId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_version_id", nullable = false)
    private CurriculumVersion curriculumVersion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requirement_id", nullable = false)
    private Requirement requirement;

    @Column(name = "recognized_credit")
    private BigDecimal recognizedCredit;

    @Enumerated(EnumType.STRING)
    @Column(name = "policy", nullable = false)
    private DuplicatePolicy policy;

    @Column(name = "priority")
    private Integer priority;
}
