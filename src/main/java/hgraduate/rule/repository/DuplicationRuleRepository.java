package hgraduate.rule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.rule.entity.DuplicationRule;

public interface DuplicationRuleRepository extends JpaRepository<DuplicationRule, Long> {
}
