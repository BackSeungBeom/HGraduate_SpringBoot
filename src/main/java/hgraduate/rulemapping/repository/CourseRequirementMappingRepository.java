package hgraduate.rulemapping.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.rulemapping.entity.CourseRequirementMapping;

public interface CourseRequirementMappingRepository extends JpaRepository<CourseRequirementMapping, Long> {
}
