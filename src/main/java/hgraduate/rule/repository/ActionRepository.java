package hgraduate.rule.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.rule.entity.Action;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
