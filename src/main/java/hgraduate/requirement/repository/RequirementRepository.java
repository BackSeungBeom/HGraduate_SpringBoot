package hgraduate.requirement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.requirement.entity.Requirement;

public interface RequirementRepository extends JpaRepository<Requirement, Long> {
}
