package com.himartclone;

import com.himartclone.common.web.Login;
import com.himartclone.member.domain.Member;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String homeLoginV3Spring(
//            @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member loginMember,
            @Login Member loginMember, // ArgumentResolver 작동
            Model model) {

        //세션에 회원 데이터가 없으면 home
        if (loginMember == null) {
            return "home";
        }

        //세션이 유지되면 로그인으로 이동
        model.addAttribute("member", loginMember);
        return "loginHome";
    }
}
