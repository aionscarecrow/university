package ua.com.foxminded.university.domain.entities;

public class Student extends Member {
	
	public static final int MEMBER_TYPE = 2; 
	
	public Student() {
		super();
	}

	
	public Student(String firstName, String lastName) {
		super(firstName, lastName);
		this.setTypeId(MEMBER_TYPE);
	}

	
	public Student(Member member) {
		super(member);
		this.setTypeId(MEMBER_TYPE);
	}
}
