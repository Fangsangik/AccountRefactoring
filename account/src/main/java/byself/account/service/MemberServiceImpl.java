package byself.account.service;

import byself.account.domain.Member;
import byself.account.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService{

    private final MemberRepository memberRepository;

    @Override
    public void join(Member member) {
        memberRepository.save(member);
    }

    @Override
    public Member findMember(String name) {
        return memberRepository.findByName(name);
    }

    @Override
    public Member getAccount(Long id) {
        if (id < 0){
            throw new RuntimeException("Null");
        }
        return memberRepository.findById(id).get();
    }
}
