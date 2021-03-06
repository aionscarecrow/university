package ua.com.foxminded.university.domain.entities;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "lectures")
public class Lecture {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "lecture_id")
	private int lectureId;
	
	@Column(name = "date", nullable = false)
	private LocalDateTime date;
	
	@ManyToOne
	@JoinColumn(name = "course_id", nullable = false)
	private Course course;
	
	@ManyToOne
	@JoinColumn(name = "teacher_id", nullable = false)
	private Member teacher;
	
	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "lecture_student", 
			joinColumns = @JoinColumn(name = "lecture_id"),
			inverseJoinColumns = @JoinColumn(name = "student_id"))
	private Set<Member> students = new HashSet<>();

	@Transient
	private DateTimeFormatter formatter = 
			DateTimeFormatter.ofPattern("uuuu-MM-dd' 'HH:mm:ss");

	
	public Lecture() {
	}
	
	public Lecture(LocalDateTime date, Course course, Teacher teacher) {
		this.date = date;
		this.course = course;
		this.teacher = teacher;
	}

	public Integer getLectureId() {
		return lectureId;
	}

	public void setLectureId(Integer lectureId) {
		this.lectureId = lectureId;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}
	
	public void setDate(String date) {
		this.date = LocalDateTime.parse(date, formatter);
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Member getTeacher() {
		return teacher;
	}

	public void setTeacher(Member teacher) {
		this.teacher = teacher;
	}

	public List<Member> getStudents() {
		return new LinkedList<>(students);
	}

	public void addStudent(Member student) {
		students.add(student);
	}
	
	public void removeStudent(Member student) {
		students.remove(student);
	}
	
	

	@Override
	public String toString() {
		StringBuilder outputBuilder = new StringBuilder();
		outputBuilder.append("Lecture ID: " + this.lectureId + "\n");
		outputBuilder.append("Date: " + this.date + "\n");
		outputBuilder.append(this.course + "\n");
		outputBuilder.append(this.teacher + "\n");
		students.stream().forEach(student -> outputBuilder.append(student + "\n"));
		return outputBuilder.toString();
	}
	
	public String stringify() {
		return "\nId: " + lectureId + ", Date: " + date + ", " + teacher + ", "
				+ " " + course +", Students: " + students.size();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((course == null) ? 0 : course.hashCode());
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((students == null) ? 0 : students.hashCode());
		result = prime * result + ((teacher == null) ? 0 : teacher.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Lecture other = (Lecture) obj;
		if (course == null) {
			if (other.course != null)
				return false;
		} else if (!course.equals(other.course))
			return false;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (students == null) {
			if (other.students != null)
				return false;
		} else if (!students.equals(other.students))
			return false;
		if (teacher == null) {
			if (other.teacher != null)
				return false;
		} else if (!teacher.equals(other.teacher))
			return false;
		return true;
	}

}
