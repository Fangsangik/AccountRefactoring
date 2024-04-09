package byself.account.dto;

import byself.account.type.TransactionType;
import byself.account.type.TransactionalResultType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QueryTransactionResponse {

    private String accountNumber;
    private TransactionType transactionType;
    private TransactionalResultType transactionalResultType;
    private String transactionId;
    private Long amount;
    private LocalDateTime transactedAt;

    public static QueryTransactionResponse from(TransactionDto transactionDto){
        return QueryTransactionResponse.builder()
                .transactionId(transactionDto.getTransactionId())
                .transactedAt(transactionDto.getTrasactedAt())
                .amount(transactionDto.getAmount())
                .transactionalResultType(transactionDto.getTransactionalResultType())
                .transactionType(transactionDto.getTransactionType())
                .accountNumber(transactionDto.getAccountNumber())
                .build();
    }
}
