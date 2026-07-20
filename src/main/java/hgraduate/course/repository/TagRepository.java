package hgraduate.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.course.entity.Tag;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
