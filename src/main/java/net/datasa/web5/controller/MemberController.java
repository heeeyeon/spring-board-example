package net.datasa.web5.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datasa.web5.domain.dto.MemberDTO;
import net.datasa.web5.security.AuthenticatedUser;
import net.datasa.web5.service.MemberService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 회원 관련 콘트롤러
 */

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("member")
public class MemberController {

    private final MemberService memberService;

    /**
     * 로그인폼으로 이동
     * @return 로그인 HTML
     */
    @GetMapping("loginForm")
    public String loginForm() {
        return "memberView/loginForm";
    }

    /**
     * 가입폼으로 이동
     * @return 가입폼 HTML
     */
    @GetMapping("join")
    public String join() {
        return "memberView/joinForm";
    }

    /**
     * 가입 정보를 입력받아 회원으로 등록하고 메인 페이지로 리다이렉트한다.
     * @param member 사용자가 입력한 가입 정보
     * @return 메인 페이지 리다이렉트 URL
     */
    @PostMapping("join")
    public String join(@ModelAttribute MemberDTO member) {
        log.debug("전달된 가입정보 : ", member);

        memberService.join(member);
        return "redirect:/";
    }

    /**
     * 아이디 중복 확인 폼으로 이동
     */
    @GetMapping("idCheck")
    public String idCheck() {
        return "/memberView/idCheck";
    }

    /**
     * 아이디 중복 확인
     * @param searchId 검색할 ID
     */
    @PostMapping("idCheck")
    public String idcheck(@RequestParam("searchId") String searchId, Model model) {
        log.debug("검색할 ID : {}", searchId);
        boolean result = memberService.idCheck(searchId);

        model.addAttribute("searchId", searchId);
        model.addAttribute("result", result);

        return "/memberView/idCheck";
    }

    /**
     * 로그인한 사용자의 정보를 모델에 저장하고 개인정보 수정화면으로 이동
     * @param user   로그인한 사용자 정보
     * @param model  모델
     * @return 수정폼 HTML
     */
    @GetMapping("info")
    public String info(@AuthenticationPrincipal AuthenticatedUser user, Model model) {
        log.debug("UserDetails 정보 : {}", user);
        MemberDTO member = memberService.getMember(user.getUsername());
        model.addAttribute("member", member);
        return "/memberView/infoForm";
    }

    /**
     * 개인정보 수정 처리
     */
    @PostMapping("info")
    public String info(@AuthenticationPrincipal AuthenticatedUser user
            , @ModelAttribute MemberDTO member) {
        log.debug("수정할 정보 : {}", member);
        member.setMemberId(user.getUsername());

        memberService.updateMember(member);

        return "redirect:/";
    }

}
