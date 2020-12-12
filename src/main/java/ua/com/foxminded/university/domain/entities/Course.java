package ua.com.foxminded.university.domain.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "courses")
public class Course {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "course_id")
	private int courseId;
	
	@Column(name = "subject", nullable = false)
	private String subject;
	
	@Column(name = "description")
	private String description;

	
	public Course() {
		super();
	}
	
		
	public Integer getCourseId() {
		return courseId;
	}


	public void setCourseId(Integer courseId) {
		this.courseId = courseId;
	}


	public Course(String subject, String description) {
		this.subject = subject;
		this.description = description;
	}

	
	public String getSubject() {
		return subject;
	}

	
	public void setSubject(String subject) {
		this.subject = subject;
	}

	
	public String getDescription() {
		return description;
	}

	
	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public String toString() {
		return "Course: [courseId=" + courseId + ", subject=" + subject + 
				", description=" + description + "]";
	}


	@Override
	public int hashCode() {
		return Objects.hash(courseId, description, subject);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Course other = (Course) obj;
		return courseId == other.courseId
				&& Objects.equals(description, other.description)
				&& Objects.equals(subject, other.subject);
	}


	
	
	
	
}
