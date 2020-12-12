package ua.com.foxminded.university.view.controllers.resolvers;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import ua.com.foxminded.university.domain.entities.Course;
import ua.com.foxminded.university.domain.entities.Lecture;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;

public class LectureArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.getParameterType().equals(Lecture.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

		Map<String, String[]> params = webRequest.getParameterMap();
		return buildLecture(params);
		
	}
	
	private Lecture buildLecture(Map<String, String[]> params) {
		Integer lectureId = Integer.valueOf(params.get("lectureId")[0]);
		LocalDateTime date = LocalDateTime.parse(params.get("date")[0]);
		Integer courseId = Integer.valueOf(params.get("courseId")[0]);
		Integer memberId = Integer.valueOf(params.get("teacherId")[0]);
		List<Student> students = 
				buildStudents(params.getOrDefault("studentId", new String[] {}));
		
		Course course = new Course();
		course.setCourseId(courseId);
		
		Teacher teacher = new Teacher();
		teacher.setMemberId(memberId);
		
		Lecture lecture = new Lecture(date, course, teacher);
		lecture.setLectureId(lectureId);
		students.forEach(lecture::addStudent);
		
		
		return lecture;
	}
	
	private List<Student> buildStudents(String[] studentIds){
		
		Function<String, Student> studentBuilder = id -> {
			Student student = new Student();
			student.setMemberId(Integer.valueOf(id));
			return student;
		};
		
		return Arrays.stream(studentIds)
				.map(studentBuilder::apply)
				.collect(Collectors.toList());
	}
	
	
}
