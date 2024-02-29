package byself.account.controller;

import byself.account.domain.Member;
import byself.account.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RequiredArgsConstructor
public class MemberController {

    private final MemberService service;

    @GetMapping("/account")
    public Member getMember(
            @PathVariable Long id
    ) {
        return service.getAccount(id);
    }
}
