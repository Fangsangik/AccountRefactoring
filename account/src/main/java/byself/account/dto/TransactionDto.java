package byself.account.dto;

import byself.account.domain.Transaction;
import byself.account.type.TransactionType;
import byself.account.type.TransactionalResultType;
import lombok.*;

import java.time.LocalDateTime;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionDto {
    private String accountNumber;
    private TransactionType transactionType;
    private TransactionalResultType transactionalResultType;
    private Long amount;
    private Long balanceSnapShot;
    private String transactionId;
    private LocalDateTime trasactedAt;

    public static TransactionDto fromEntity(Transaction transaction){
        return TransactionDto.builder()
                .accountNumber(transaction.getAccount().getAccountNumber())
                .transactionType(transaction.getTransactionType())
                .transactionalResultType(transaction.getTransactionalResultType())
                .amount(transaction.getAmount())
                .balanceSnapShot(transaction.getBalanceSnapShot())
                .transactionId(transaction.getTransactionId())
                .trasactedAt(transaction.getTransactedAt())
                .build();
    }
}
