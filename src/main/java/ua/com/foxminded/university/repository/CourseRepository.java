package ua.com.foxminded.university.repository;

import org.springframework.data.repository.CrudRepository;

import ua.com.foxminded.university.domain.entities.Course;

public interface CourseRepository extends CrudRepository<Course, Integer>{
	
	Iterable<Course> findAllByOrderByCourseIdAsc();
}
