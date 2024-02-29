package byself.account.service;

import byself.account.domain.Member;
import byself.account.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface MemberService {
    void join(Member member);

    Member findMember(String name);

    Member getAccount(Long id);
}
