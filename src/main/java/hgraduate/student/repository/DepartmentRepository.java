package hgraduate.student.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import hgraduate.student.entity.Department;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
