package jpabook.jpashop.controller;

import jpabook.jpashop.entity.Address;
import jpabook.jpashop.entity.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Request;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @GetMapping("members/new")
    public String createForm(Model model) {
        model.addAttribute("memberForm", new MemberForm());
        return "members/createMemberForm";
    }

    @PostMapping("members/new")
    public String create(@Valid MemberForm memberForm, BindingResult bindingResult) {

        if(bindingResult.hasErrors()) {
            return "members/createMemberForm";
        }

        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());
        Member member = new Member();
        member.setName(memberForm.getName());
        member.setAddress(address);
//        memberService.join(member);
        memberService.joinWithSecurity(memberForm); // 시큐리티 사용하는 회원가입으로 변경
        return "redirect:/";
    }

    @GetMapping(value = "/members")
    public String list(Model model) {
        List<Member> members = memberService.findMembers();
        model.addAttribute("members", members);
        return "members/memberList";
    }

    @GetMapping("/login")
    public String loginForm() {
        return "members/login";
    }

    @GetMapping("/login/error")  // /login/error?msg="ggd"
    public String loginError(String msg, Model model) {
        model.addAttribute("msg", msg);
        return "members/login";
    }

    @ResponseBody
    @GetMapping("/principal")
    public String principal(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/bad")
    public void exception() {
        throw new UsernameNotFoundException("배드~");
    }

}
