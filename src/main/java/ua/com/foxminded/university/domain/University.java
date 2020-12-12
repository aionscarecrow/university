package ua.com.foxminded.university.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.domain.exceptions.DomainException;


public class University {
	
	private static final Logger log = LoggerFactory.getLogger(University.class);
	
	private List<Teacher> teachers;
	private List<Student> students;
	private LectureSchedule schedule;


	public University() {
		this.teachers = new LinkedList<>();
		this.students = new LinkedList<>();
		this.schedule = new LectureSchedule();
	}
	
	public University(UniversityData data) {
		this.teachers = data.getTeachers();
		this.students = data.getStudents();
		this.schedule = new LectureSchedule(data.getLectures());
	}
	
	public List<Teacher> getTeachers() {
		log.info("returning list of {} teachers", teachers.size());
		return teachers;
	}

	public List<Student> getStudents() {
		log.info("returning list of {} students", students.size());
		return students;
	}

	public void addMember(Member member) throws DomainException {
			log.info("adding member {}", member);
			
		if(member instanceof Student) {
			log.debug("adding member [{}] as student", member);
			
			students.add((Student) member);
			
		} else if(member instanceof Teacher) {
			log.debug("adding member [{}] as teacher", member);
			
			teachers.add((Teacher) member);
			
		} else {
			log.error("failed to add member [{}]", member);
			throw new DomainException("member type not recognized");
		}
	}
	
	public void scheduleLecture(Lecture lecture) throws DomainException {
		if(isValidLecture(lecture) && hasNoScheduleConflicts(lecture)) {
			log.info("scheduling lecture {}", lecture.stringify());
			schedule.addLecture(lecture);
		} else {
			log.error("failed to schedule lecture [{}]", lecture);
			throw new DomainException("failed to schedule lecture");
		}
	}
	
	private boolean isValidLecture(Lecture lecture) {
		
		log.debug("validating lecture [{}]", lecture);
		
		if(
				lecture == null || 
				lecture.getDate() == null ||
				lecture.getCourse() ==null ||
				lecture.getStudents().isEmpty() ||
				lecture.getTeacher() == null
			) {
			log.warn("field validation failed for [{}]", lecture);
			return false;
		}
		
		log.debug("validation passed for lecture [{}]", lecture.stringify());
		return true;
	}
	
	private boolean hasNoScheduleConflicts(Lecture newLecture) {
		log.debug("checking for schedule conflict of [{}]", newLecture.stringify());
		Member teacher = newLecture.getTeacher();
		LocalDateTime lectureTime = newLecture.getDate();
		
		for(Lecture currentLecture : schedule.getLectures()) {
			if(currentLecture.getTeacher().equals(teacher) && 
					currentLecture.getDate().truncatedTo(ChronoUnit.HOURS)
					.equals(lectureTime.truncatedTo(ChronoUnit.HOURS))
				) {
				
				log.warn("\nlecture [{}]\nconflicts with [{}]", 
						newLecture.stringify(), currentLecture.stringify());
				
				return false;
			}
		}
		
		log.debug("schedule conflict check passed for [{}]", newLecture.stringify());
		return true;
	}
	
	public void cancelLecture(Lecture lecture) throws DomainException {
		if(isValidLecture(lecture)) {
			if(schedule.contains(lecture)) {
				log.info("cancelling lecture [{}]", lecture.stringify());
				schedule.removeLecture(lecture);
				return;
			} else {
				log.error("lecture not found [{}]", lecture.stringify());
			}
		}
		throw new DomainException("failed to cancel lecture");
	}
	
	
	public LectureSchedule getMonthlySchedule(Member member) {
		log.info("getting monthly schedule for member [{}]", member);
		Month currentMonth = LocalDateTime.now().toLocalDate().getMonth();
		return filterSchedule(
				member, lecture -> lecture.getDate().getMonth().equals(currentMonth)
			);
	}
	
	public LectureSchedule getDailySchedule(Member member) {
		log.info("getting daily schedule for member [{}]", member);
		LocalDate today = LocalDateTime.now().toLocalDate();
		return filterSchedule(
				member, lecture -> lecture.getDate().toLocalDate().equals(today)
			);
	}
	
	private LectureSchedule filterSchedule(Member member, Predicate<Lecture> timeFilter) {
		LectureSchedule resultingSchedule = new LectureSchedule();
		
		log.debug("picking lectures for member [{}] by supplied Predicate", member);
		
		schedule.getLectures().stream()
		.filter(timeFilter::test)
		.filter(
				lecture -> lecture.getTeacher().equals(member) || 
				lecture.getStudents().contains(member)
				)
		.forEach(resultingSchedule::addLecture);
		
		log.debug("returning filtered schedule of {} lectures", 
											resultingSchedule.getLectures().size());
		return resultingSchedule;
	}

	public void setData(UniversityData data) {
		log.debug("adding {} teachers, {} students and {} lectures from UniversityData",
				data.getTeachers().size(), data.getStudents().size(), data.getLectures().size());
		this.teachers.addAll(data.getTeachers());
		this.students.addAll(data.getStudents());
		data.getLectures().forEach(this.schedule::addLecture);
	}

	@Override
	public String toString() {
		StringBuilder outputBuilder = new StringBuilder("University:\n");
		outputBuilder.append("Teachers: " + teachers.size() +"\n");
		outputBuilder.append("Students: " + students.size() +"\n");
		outputBuilder.append("Lectures: " + schedule.getLectures().size() + "\n\n");
		schedule.getLectures().stream().map(Lecture::stringify).forEach(outputBuilder::append);
		return outputBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((schedule == null) ? 0 : schedule.hashCode());
		result = prime * result + ((students == null) ? 0 : students.hashCode());
		result = prime * result + ((teachers == null) ? 0 : teachers.hashCode());
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
		University other = (University) obj;
		if (schedule == null) {
			if (other.schedule != null)
				return false;
		} else if (!schedule.equals(other.schedule))
			return false;
		if (students == null) {
			if (other.students != null)
				return false;
		} else if (!students.equals(other.students))
			return false;
		if (teachers == null) {
			if (other.teachers != null)
				return false;
		} else if (!teachers.equals(other.teachers))
			return false;
		return true;
	}

	
	
	
}
