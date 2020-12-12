package ua.com.foxminded.university.domain.entities;

public class Teacher extends Member {
	
	public static final int MEMBER_TYPE = 1; 
	
	public Teacher() {
		super();
		this.setTypeId(MEMBER_TYPE);
	}

	
	public Teacher(String firstName, String lastName) {
		super(firstName, lastName);
		this.setTypeId(MEMBER_TYPE);
	}

	
	public Teacher(Member member) {
		super(member);
		this.setTypeId(MEMBER_TYPE);
	}
	
}
