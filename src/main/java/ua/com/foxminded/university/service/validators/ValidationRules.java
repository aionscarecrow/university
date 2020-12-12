package ua.com.foxminded.university.service.validators;

import java.util.Objects;
import java.util.function.Predicate;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;

public final class ValidationRules {

	public enum MemberRules implements EntityRules<Member> {
		
		NOT_NULL(new Rule<>(Objects::nonNull, "is not null")),
		HAS_ID(new Rule<>(m -> m.getMemberId() > 0, "has valid memberId set")),
		HAS_FIRST_NAME(new Rule<>(m -> m.getFirstName() != null, "has name")),
		HAS_LAST_NAME(new Rule<>(m -> m.getLastName() != null, "has last name")),
		HAS_TYPE_ID(new Rule<>(
				m -> m.getTypeId() == Teacher.MEMBER_TYPE || 
					m.getTypeId() == Student.MEMBER_TYPE, 
				"has valid typeId set"));
		
		private Rule<Member> rule;
		
		private MemberRules(Rule<Member> rule) {
			this.rule = rule;
		}

		@Override
		public Rule<Member> getRule() {
			return rule;
		}
	}

	public enum LectureRules implements EntityRules<Lecture> {
		
		NOT_NULL(new Rule<>(Objects::nonNull, "is not null")),
		HAS_ID(new Rule<>(l -> l.getLectureId() > 0, "has valid lectureId set")),
		HAS_DATE(new Rule<>(l -> l.getDate() != null, "has date set")),
		HAS_COURSE(new Rule<>(l -> l.getCourse() != null, "has course set")),
		HAS_TEACHER(new Rule<>(l -> l.getTeacher() != null, "has teacher set")),
		HAS_VALID_TEACHER(new Rule<>(
				l -> l.getTeacher() != null && 
				l.getTeacher().getTypeId() == Teacher.MEMBER_TYPE, 
				"has valid teacher")),
		HAS_VALID_STUDENTS(new Rule<>(
				l -> l.getStudents().stream()
						.allMatch(m -> m.getTypeId() == Student.MEMBER_TYPE), 
				"has valid students"));
		
		private Rule<Lecture> rule;
		
		private LectureRules(Rule<Lecture> rule) {
			this.rule = rule;
		}
		
		@Override
		public Rule<Lecture> getRule() {
			return rule;
		}
	}

	public enum CourseRules implements EntityRules<Course> {
		
		NOT_NULL(new Rule<>(Objects::nonNull, "is not null")),
		HAS_ID(new Rule<>(c -> c.getCourseId() > 0, "has valid courseId")),
		HAS_SUBJECT(new Rule<>(c -> c.getSubject() != null,  "has subject set")),
		HAS_DESCRIPTION(new Rule<>(c -> c.getDescription() != null, "has description"));
		
		private Rule<Course> rule;
		
		private CourseRules(Rule<Course> rule) {
			this.rule = rule;
		}

		@Override
		public Rule<Course> getRule() {
			return rule;
		}
	}
}


interface EntityRules<T> {
	Rule<T> getRule();
}

class Rule<T> {
	
	private Predicate<T> definition;
	private String description;
	
	public Rule(Predicate<T> definition, String description) {
		this.definition = definition;
		this.description = description;
	}
	
	public Predicate<T> getDefinition() {
		return definition;
	}
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		return "Rule [description=" + description + "]";
	}
}




