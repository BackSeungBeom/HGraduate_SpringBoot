package hgraduate.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.course.entity.CourseTag;
import hgraduate.course.entity.CourseTagId;

public interface CourseTagRepository extends JpaRepository<CourseTag, CourseTagId> {
}
