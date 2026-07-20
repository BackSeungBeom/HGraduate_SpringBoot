package hgraduate.requirement.entity;

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

// Not a distinct entity in system-design.md; kept separate because requirements.xlsx is its own real table (Schema_Difference_Report_v1.1.md §6).
@Entity
@Table(name = "requirements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Requirement {

    @Id
    @Column(name = "requirement_id")
    private Long requirementId;

    @Column(name = "requirement_code", nullable = false, unique = true)
    private String requirementCode;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private RequirementCategory category;

    @Column(name = "name", nullable = false)
    private String name;
}
