package byself.account.domain;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter // test 때문에 잠시 사용
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
public class AccountUser extends BaseEntity{
    private String name;

    @OneToMany(mappedBy = "accountUser")
    List<Account> accounts = new ArrayList<>();

    public AccountUser(){

    }

}
