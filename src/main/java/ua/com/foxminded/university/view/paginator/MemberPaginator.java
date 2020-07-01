package ua.com.foxminded.university.view.paginator;

import java.util.Optional;

import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;
import org.springframework.web.context.WebApplicationContext;

import ua.com.foxminded.university.domain.entities.Member;

@Component
@Scope(value = WebApplicationContext.SCOPE_SESSION, proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MemberPaginator<T extends Member> 
	extends Paginator<T> implements EntryQueryablePaginator<T> {
	
	public MemberPaginator() {
		super(60, 10);
	}
	
	@Override
	public Optional<T> getEntry(int key) {
		log.debug("Entry requested by key [{}]", key);
		return getData().stream()
				.filter(o -> o.getMemberId() == key)
				.findFirst();
	}
}
