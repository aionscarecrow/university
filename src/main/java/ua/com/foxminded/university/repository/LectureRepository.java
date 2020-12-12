package ua.com.foxminded.university.repository;

import java.time.LocalDateTime;

import org.springframework.data.repository.CrudRepository;

import ua.com.foxminded.university.domain.entities.Lecture;

public interface LectureRepository extends CrudRepository<Lecture, Integer>{
	
	Iterable<Lecture> findAllByOrderByLectureIdAsc();
	
	Integer countByStudentsMemberIdEquals(int id);
	
	Iterable<Lecture> findByDateBetween(
			LocalDateTime from, LocalDateTime to);
	
}
