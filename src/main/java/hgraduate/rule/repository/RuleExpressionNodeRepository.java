package hgraduate.rule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.rule.entity.RuleExpressionNode;

public interface RuleExpressionNodeRepository extends JpaRepository<RuleExpressionNode, Long> {
}
