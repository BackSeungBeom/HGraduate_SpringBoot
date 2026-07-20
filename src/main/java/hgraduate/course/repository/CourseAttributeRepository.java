package hgraduate.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.course.entity.CourseAttribute;

public interface CourseAttributeRepository extends JpaRepository<CourseAttribute, Long> {
}
