package byself.account.repository;

import byself.account.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {

    private static final Map<Long, Member> store = new HashMap<>();


    @Override
    public void save(Member member) {
        store.put(member.getId(), member);
    }

    @Override
    public Member findByName(String name) {
        return store.get(name);
    }

    @Override
    public Optional<Member> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Member> findByAccountNumber(String accountNum) {
        return new ArrayList<>(store.values());
    }
}
