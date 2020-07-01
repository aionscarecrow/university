package ua.com.foxminded.university.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Lazy;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import ua.com.foxminded.university.dao.CourseDao;
import ua.com.foxminded.university.dao.LectureDao;
import ua.com.foxminded.university.dao.MemberDao;
import ua.com.foxminded.university.dao.UniversityDao;
import ua.com.foxminded.university.dao.db.CourseDaoImpl;
import ua.com.foxminded.university.dao.db.LectureDaoImpl;
import ua.com.foxminded.university.dao.db.MemberDaoImpl;
import ua.com.foxminded.university.dao.db.UniversityDaoImpl;

@Configuration
@ComponentScan({
	"ua.com.foxminded.university.dao",
	"ua.com.foxminded.university.service"
	})
@EnableTransactionManagement(proxyTargetClass = true)
public class RootConfig implements TransactionManagementConfigurer {
	
	@Value("${envTarget:default}")
	String envTarget;
	
	@Bean
	@Lazy
    public DataSource dataSource() {
		final JndiDataSourceLookup dsLookup = new JndiDataSourceLookup();
        return dsLookup.getDataSource("jdbc/" + envTarget);
    }
	
	
	@Bean
	public PlatformTransactionManager txManager() {
		return new DataSourceTransactionManager(dataSource());
	}
	
	
	@Override
	public TransactionManager annotationDrivenTransactionManager() {
		return txManager();
	}

	
	@Bean
	public MemberDao memberDao() {
		MemberDaoImpl memberDao = new MemberDaoImpl();
		memberDao.setDataSource(dataSource());
		
		return memberDao;
	}
	
	
	@Bean
	@DependsOn({"memberDao", "courseDao"})
	public LectureDao lectureDao() {
		LectureDaoImpl lectureDao = new LectureDaoImpl();
		lectureDao.setDataSource(dataSource());
		
		return lectureDao;
	}
	
	
	@Bean
	public CourseDao courseDao() {
		CourseDaoImpl courseDao = new CourseDaoImpl();
		courseDao.setDataSource(dataSource());
		
		return courseDao;
	}
	
	
	@Bean
	@DependsOn({"memberDao", "lectureDao"})
	public UniversityDao universityDao() {
		UniversityDaoImpl universityDao = new UniversityDaoImpl();
		universityDao.setDataSource(dataSource());
		
		return universityDao;
	}
}
