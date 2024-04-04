package byself.account.domain;

import byself.account.type.AccountStatus;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Builder
@EntityListeners(AuditingEntityListener.class)
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity{

    @ManyToOne
    @JoinColumn(name = "ACCOUNTUSER_ID")
    private AccountUser accountUser;

    private Long balance;
    private String accountNumber;
    private LocalDateTime registeredAt;
    private LocalDateTime unregisteredAt;

    @Enumerated(EnumType.STRING)
    private AccountStatus accountStatus;


    public Account(Long balance, String accountNumber) {
        this.balance = balance;
        this.accountNumber = accountNumber;
    }
}
