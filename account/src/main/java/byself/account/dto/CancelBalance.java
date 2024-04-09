package byself.account.dto;

import byself.account.type.TransactionalResultType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

public class CancelBalance {

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        @NotBlank
        private String transactionId;

        @NotBlank
        @Size(max = 10, min = 5)
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
    public static class Response {
        private String accountNumber;
        private TransactionalResultType transactionalResultType;
        private String transactionId;
        private Long amount;
        private LocalDateTime transactedAt;

        public static Response from(TransactionDto transactionDto) {
            return Response.builder()
                    .accountNumber(transactionDto.getAccountNumber())
                    .amount(transactionDto.getAmount())
                    .transactedAt(transactionDto.getTrasactedAt())
                    .transactionId(transactionDto.getTransactionId())
                    .transactionalResultType(transactionDto.getTransactionalResultType())
                    .build();
        }
    }
}
