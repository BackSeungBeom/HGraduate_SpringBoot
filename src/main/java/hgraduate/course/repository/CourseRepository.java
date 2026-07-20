package hgraduate.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.course.entity.Course;

public interface CourseRepository extends JpaRepository<Course, Long> {
}
