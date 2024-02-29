package byself.account.config;

import byself.account.repository.MemberRepository;
import byself.account.repository.MemberRepositoryImpl;
import byself.account.service.MemberService;
import byself.account.service.MemberServiceImpl;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {
    public MemberService memberService(){
        return new MemberServiceImpl(memberRepository());
    }

    public MemberRepository memberRepository(){
        return new MemberRepositoryImpl();
    }
}
