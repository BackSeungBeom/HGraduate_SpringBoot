package hgraduate.requirement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.requirement.entity.RequirementCategory;

public interface RequirementCategoryRepository extends JpaRepository<RequirementCategory, Long> {
}
