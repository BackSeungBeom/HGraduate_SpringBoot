package hgraduate.rule.entity;

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

// rule_action.xlsx — a rule can have >1 Action row (e.g. rule 10), unlike system-design.md's single action_id FK on Rule.
@Entity
@Table(name = "rule_action")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Action {

    @Id
    @Column(name = "action_id")
    private Long actionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private GraduationRule rule;

    @Column(name = "action_type", nullable = false)
    private String actionType;

    @Column(name = "target", nullable = false)
    private String target;

    @Column(name = "value")
    private Double value;

    @Column(name = "priority")
    private Integer priority;
}
