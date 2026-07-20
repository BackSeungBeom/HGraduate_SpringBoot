package hgraduate.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.course.entity.Prerequisite;

public interface PrerequisiteRepository extends JpaRepository<Prerequisite, Long> {
}
