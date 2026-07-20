package hgraduate.curriculum.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.curriculum.entity.CurriculumVersion;

public interface CurriculumVersionRepository extends JpaRepository<CurriculumVersion, Long> {
}
