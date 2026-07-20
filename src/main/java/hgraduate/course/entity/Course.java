package hgraduate.course.entity;

import java.time.LocalDateTime;

import hgraduate.curriculum.entity.CurriculumVersion;
import hgraduate.student.entity.Department;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

// _legacy_-prefixed fields (schema-migration-spec.md §3, Group B) — do not read from calculation logic.
@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {

    @Id
    @Column(name = "course_id")
    private Long courseId;

    @Column(name = "course_code", nullable = false, unique = true)
    private String courseCode;

    @Column(name = "course_name_ko", nullable = false)
    private String courseNameKo;

    @Column(name = "course_name_en")
    private String courseNameEn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "curriculum_version_id", nullable = false)
    private CurriculumVersion curriculumVersion;

    @Column(name = "course_type")
    private String courseType;

    @Column(name = "recommended_grade")
    private Integer recommendedGrade;

    @Column(name = "recommended_semester")
    private Integer recommendedSemester;

    @Column(name = "is_open", nullable = false)
    private boolean open;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "_legacy_category")
    private String legacyCategory;

    @Column(name = "_legacy_subcategory")
    private String legacySubcategory;

    @Column(name = "_legacy_is_major", nullable = false)
    private boolean legacyIsMajor;

    @Column(name = "_legacy_is_general", nullable = false)
    private boolean legacyIsGeneral;

    @Column(name = "_legacy_is_required", nullable = false)
    private boolean legacyIsRequired;

    @Column(name = "_legacy_is_elective", nullable = false)
    private boolean legacyIsElective;
}
