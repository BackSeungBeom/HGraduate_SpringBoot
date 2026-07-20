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

// scope_type (GLOBAL/DEPARTMENT) from system-design.md §1.4 is not present in seed-data v1.1 — not added here.
@Entity
@Table(name = "category_tree")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequirementCategory {

    @Id
    @Column(name = "category_id")
    private Long categoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_category_id")
    private RequirementCategory parentCategory;

    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @Column(name = "display_order")
    private Integer displayOrder;
}
