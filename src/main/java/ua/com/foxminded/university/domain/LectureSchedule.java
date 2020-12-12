package ua.com.foxminded.university.domain;

import java.util.LinkedList;
import java.util.List;

import ua.com.foxminded.university.domain.entities.Lecture;

public class LectureSchedule {
	
	private List<Lecture> lectures;

	public LectureSchedule() {
		lectures = new LinkedList<>();
	}
	
	public LectureSchedule (List<Lecture> lectures) {
		this.lectures = new LinkedList<>(lectures);
	}
	
	public void addLecture(Lecture lecture) {
		lectures.add(lecture);
	}
	
	public void removeLecture(Lecture lecture) {
		lectures.remove(lecture);
	}
	
	public List<Lecture> getLectures() {
		return this.lectures;
	}
	
	public boolean contains(Lecture lecture) {
		return this.lectures.contains(lecture);
	}

	@Override
	public String toString() {
		StringBuilder outputBuilder = new StringBuilder();
		lectures.forEach(lecture -> outputBuilder.append(lecture.toString() + "\n"));
		return outputBuilder.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((lectures == null) ? 0 : lectures.hashCode());
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
		LectureSchedule other = (LectureSchedule) obj;
		if (lectures == null) {
			if (other.lectures != null)
				return false;
		} else if (!lectures.equals(other.lectures))
			return false;
		return true;
	}
	
	
}
