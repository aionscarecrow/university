package ua.com.foxminded.university;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import ua.com.foxminded.university.view.controllers.resolvers.LectureArgumentResolver;

@SpringBootApplication
public class UniversityApp implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(UniversityApp.class, args);

	}
	
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
		resolvers.add(new LectureArgumentResolver());
	}
}

