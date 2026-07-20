package hgraduate.course.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// schema-migration-spec.md §3, Group A — permanent columns, split out of courses in v1.1; course_id is shared PK/FK with Course.
@Entity
@Table(name = "course_attributes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseAttribute {

    @Id
    @Column(name = "course_id")
    private Long courseId;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "course_id")
    private Course course;

    @Column(name = "credit")
    private BigDecimal credit;

    @Column(name = "lecture_credit")
    private BigDecimal lectureCredit;

    @Column(name = "lab_credit")
    private BigDecimal labCredit;

    @Column(name = "design_credit")
    private BigDecimal designCredit;

    @Column(name = "english_ratio")
    private BigDecimal englishRatio;
}
