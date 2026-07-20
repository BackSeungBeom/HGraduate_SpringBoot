package hgraduate.rule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.rule.entity.GraduationRule;

public interface GraduationRuleRepository extends JpaRepository<GraduationRule, Long> {
}
