package byself.account.domain;

import byself.account.type.TransactionType;
import byself.account.type.TransactionalResultType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ManyToOne;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Transaction extends BaseEntity{
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;

    @Enumerated(EnumType.STRING)
    private TransactionalResultType transactionalResultType;

    @ManyToOne
    private Account account;

    private Long amount;
    private Long balanceSnapShot;

    private String transactionId;
    private LocalDateTime transactedAt;

}
