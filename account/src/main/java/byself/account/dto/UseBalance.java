package byself.account.dto;

import byself.account.aop.AccountLockInterface;
import byself.account.type.TransactionalResultType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class UseBalance {

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Request implements AccountLockInterface {

        @NotNull
        @Min(1)
        private Long id;

        @NotBlank
        @Size(min =1 , max = 10)
        private String accountNumber;

        @NotNull
        @Min(10)
        @Max(1000_000_000)
        private Long amount;

    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response{
        private String accountNumber;
        private TransactionalResultType transactionalResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto transactionDto){
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .amount(transactionDto.getAmount())
                    .transactionalResultType(transactionDto.getTransactionalResultType())
                    .transactedAt(transactionDto.getTrasactedAt())
                    .transactionId(transactionDto.getTransactionId())
                    .build();
        }
    }
}
