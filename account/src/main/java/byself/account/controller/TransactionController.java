package byself.account.controller;

import byself.account.aop.AccountLock;
import byself.account.dto.CancelBalance;
import byself.account.dto.QueryTransactionResponse;
import byself.account.dto.UseBalance;
import byself.account.exception.AccountException;
import byself.account.service.TransactionService;
import byself.account.service.TransactionServiceImpl;
import jakarta.validation.Valid;
import jdk.jfr.Frequency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
public class TransactionController {

    private final TransactionServiceImpl transactionService;

    @PostMapping("/transaction/use")
    @AccountLock
    public UseBalance.Response useBalance(
            @Valid @RequestBody UseBalance.Request request
    ) throws InterruptedException {
        try {
            Thread.sleep(2000);
            return UseBalance.Response.from(
                    transactionService.useBalance(
                            request.getId(),
                            request.getAccountNumber(),
                            request.getAmount()
                    )
            );
        } catch (AccountException e){
            log.error("Failed to use balance");
            transactionService.saveFailedUseTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        }
    }


    @PostMapping("/transsaction/cancel")
    @AccountLock
    public CancelBalance.Response cancelBalance(
            @Valid
            @RequestBody CancelBalance.Request request
    ) {
        try {
            return CancelBalance.Response.from(
                    transactionService.cancelBalance(
                            request.getTransactionId(),
                            request.getAccountNumber(),
                            request.getAmount()
                    )
            );
        } catch (AccountException e){
            log.error("failed to use balance");
            transactionService.saveFailedCancelTransaction(
                    request.getAccountNumber(),
                    request.getAmount()
            );
            throw e;
        }
    }

    @GetMapping("/transaction/{transactionId}")
    @AccountLock
    public QueryTransactionResponse queryTransactionResponse (
            @PathVariable String transactionId
    ) {
        return QueryTransactionResponse.from(transactionService.queryTransacation(transactionId));
    }
}
