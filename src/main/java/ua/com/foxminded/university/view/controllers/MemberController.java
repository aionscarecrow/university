package ua.com.foxminded.university.view.controllers;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import ua.com.foxminded.university.domain.entities.Member;
import ua.com.foxminded.university.domain.entities.Student;
import ua.com.foxminded.university.domain.entities.Teacher;
import ua.com.foxminded.university.service.MemberService;
import ua.com.foxminded.university.service.exceptions.ServiceException;
import ua.com.foxminded.university.view.paginator.EntryQueryablePaginator;
import ua.com.foxminded.university.view.paginator.PageData;

@Controller
public class MemberController {
	
	@Autowired
	private MemberService memberService;
	
	@Autowired
	@Qualifier("memberPaginator")
	private EntryQueryablePaginator<Member> pagination;
	
	public static final String TABLE_FRAGMENT = "fragments/members :: membersTable";
	public static final String EDIT_FRAGMENT = "fragments/members :: memberForm";
	
	
	private static final Logger log = LoggerFactory.getLogger(MemberController.class);
	
	@GetMapping("/members")
	public ModelAndView listMembers(@RequestParam(defaultValue = "1") int page,
			@RequestParam(required = false) String fetch) throws ServiceException {
		
		log.debug("listMembers request params: page [{}], fetch [{}]", page, fetch);
		
		ModelAndView modelView = new ModelAndView();
		modelView.addObject("pageData", getMembersPageData(page, fetch));
		modelView.setViewName(TABLE_FRAGMENT);
		
		return modelView;
	}
	
	PageData<Member> getMembersPageData(int page, String fetch, Class<?>...classes) 
			throws ServiceException {
		if(fetch != null || !pagination.hasValidCache()) {
			pagination.setData(memberService.retrieveAll(), Student.class, Teacher.class);
		}
		
		return pagination.getPageData(page, classes);
	}
	
	
	Member getMemberById(int id) throws ServiceException {
		if(pagination.hasValidCache()) {
			Optional<Member> member = pagination.getEntry(id);
			if(member.isPresent()) {
				log.info("returning member from paginator [{}]", member.get());
				return member.get();
			}
		}
		log.info("returning member from service");
		return memberService.retrieveById(id);
	}
	
	
	@PostMapping("/deleteMember")
	public ModelAndView delete(@RequestParam(required = true) int id,
			@RequestParam Optional<Integer> page) throws ServiceException {
		
		log.debug("delete request params: id [{}], page [{}]", id, page);
		
		Member member = new Member();
		member.setMemberId(id);
		
		memberService.delete(member);
		pagination.invalidateCache();
		
		return listMembers(page.orElse(1), "fetch");
	}
	
	
	@GetMapping("/memberForm")
	public ModelAndView getForm(@RequestParam(defaultValue = "0") int id,
			@RequestParam Optional<Integer> page)	throws ServiceException {
		
		log.debug("form request params: id [{}], page [{}]", id, page);
		
		ModelAndView modelView = new ModelAndView();
		Member member;
		
		if(id > 0) {
			member = getMemberById(id);
			modelView.addObject("id", id);
			modelView.addObject("page", page.orElse(1));
		} else {
			member = new Member();
		}
		modelView.addObject("url", "/saveMember");
		modelView.addObject("member", member);
		modelView.setViewName(EDIT_FRAGMENT);
		
		return modelView;
	}
	

	@PostMapping("/saveMember")
	public ModelAndView save(@RequestParam Optional<Integer> page,
			Member member) throws ServiceException {
		
		if(member.getMemberId() > 0) {
			log.debug("update request params: [{}] , page [{}]", member, page);
			memberService.update(member);
		} else {
			log.debug("create request params: [{}] , page [{}]", member, page);
			memberService.create(member);
		}
		
		pagination.invalidateCache();
		return listMembers(page.orElse(Integer.MAX_VALUE), "fetch");
	}
	
}
