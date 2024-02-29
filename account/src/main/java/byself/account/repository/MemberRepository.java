package byself.account.repository;

import byself.account.domain.Member;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

public interface MemberRepository {
    void save(Member member);

    Member findByName(String name);

    Optional<Member> findById(Long id);

    List<Member> findByAccountNumber(String accountNum);
}
