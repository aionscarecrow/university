package ua.com.foxminded.university.domain;

import java.util.LinkedList;
import java.util.List;

import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;

public final class UniversityData {
	
	private List<Teacher> teachers;
	private List<Student> students;
	private List<Lecture> lectures;
	
	public UniversityData() {
		this.teachers = new LinkedList<>();
		this.students = new LinkedList<>();
		this.lectures = new LinkedList<>();
	}

	public void setTeachers(List<Teacher> teachers) {
		this.teachers.addAll(teachers);
	}

	public void setStudents(List<Student> students) {
		this.students.addAll(students);
	}

	public void setLectures(List<Lecture> lectures) {
		this.lectures.addAll(lectures);
	}

	public List<Teacher> getTeachers() {
		return teachers;
	}

	public List<Student> getStudents() {
		return students;
	}

	public List<Lecture> getLectures() {
		return lectures;
	}
}
