package com.himartclone.login.service;

import com.himartclone.member.domain.Member;
import com.himartclone.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginService {

    private final MemberRepository memberRepository;

    /**
     * @return null이면 로그인 실패
     */
    public Member login(String loginId, String password) {
        return memberRepository.findByMemberId(loginId)
                .filter(m -> m.getPassword().equals(password))
                .orElse(null);
    }
}
