package hgraduate.course.entity;

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
@Table(name = "prerequisites")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prerequisite {

    @Id
    @Column(name = "prerequisite_id")
    private Long prerequisiteId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_course_id", nullable = false)
    private Course prerequisiteCourse;

    @Enumerated(EnumType.STRING)
    @Column(name = "relation_type", nullable = false)
    private PrerequisiteRelationType relationType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_version_id", nullable = false)
    private CurriculumVersion curriculumVersion;
}
