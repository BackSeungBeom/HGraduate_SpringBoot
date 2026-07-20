package hgraduate.rule.entity;

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
@Table(name = "rule_expression")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleExpressionNode {

    @Id
    @Column(name = "expression_id")
    private Long expressionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rule_id", nullable = false)
    private GraduationRule rule;

    @Enumerated(EnumType.STRING)
    @Column(name = "expression_type", nullable = false)
    private ExpressionType expressionType;

    @Column(name = "left_operand")
    private String leftOperand;

    @Column(name = "operator")
    private String operator;

    @Column(name = "right_operand")
    private String rightOperand;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_expression")
    private RuleExpressionNode parentExpression;

    @Column(name = "\"order\"")
    private Integer sortOrder;
}
