package hgraduate.requirement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.requirement.entity.GraduationRequirement;

public interface GraduationRequirementRepository extends JpaRepository<GraduationRequirement, Long> {
}
