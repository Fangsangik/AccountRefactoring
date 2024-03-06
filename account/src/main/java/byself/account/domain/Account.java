package byself.account.domain;

import byself.account.exception.AccountException;
import byself.account.type.AccountStatus;
import byself.account.type.ErrorCode;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

import static byself.account.type.ErrorCode.*;

@Getter
@Setter
@RequiredArgsConstructor
@Entity
@EntityListeners(AuditingEntityListener.class)
public class Account extends BaseEntity{

    @ManyToOne
    private AccountUser accountUser;
    private String accountNumber;
    private Long balance;
    private String password;
    private LocalDateTime registerDate;
    private LocalDateTime unRegisterDate;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;

    public void useBalance(Long amount) {
        if (amount > balance) {
            throw new AccountException(AMOUNT_EXCEED_BALANCE);
        } else {
            balance -= amount;
        }
    }

    public void cancelBalance(Long amount) {
        if (amount <= 0){
            throw new AccountException(INVALID_REQUEST);
        } else {
            balance += amount;
        }
    }
}
