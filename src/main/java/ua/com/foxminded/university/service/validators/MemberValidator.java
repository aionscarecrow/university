package ua.com.foxminded.university.service.validators;

import org.springframework.stereotype.Component;

import ua.com.foxminded.university.domain.entities.Member;

@Component
public final class MemberValidator<T extends Member> extends Validator<T> {

}
