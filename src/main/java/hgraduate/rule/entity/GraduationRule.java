package hgraduate.rule.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

// Legacy flat rule structure, NOT system-design.md's "Rule" entity — see Validation_Report_v1.1.md §5 (8/28 rules have semantic gaps vs rule_expression/rule_action).
@Entity
@Table(name = "graduation_rules")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GraduationRule {

    @Id
    @Column(name = "rule_id")
    private Long ruleId;

    @Column(name = "rule_type", nullable = false)
    private String ruleType;

    @Column(name = "\"function\"", nullable = false)
    private String function;

    @Column(name = "target", nullable = false)
    private String target;

    @Column(name = "operator")
    private String operator;

    @Column(name = "value")
    private String value;

    @Column(name = "action", nullable = false)
    private String action;

    @Column(name = "priority")
    private Integer priority;
}
