package byself.account.service;

import byself.account.dto.TransactionDto;
import org.springframework.stereotype.Service;

@Service
public interface TransactionService {

    TransactionDto useBalance(Long userId, String accountNumber, Long amount);

    void saveFailedUseTransaction(String accountNumber, Long amount);

    TransactionDto cancelBalance(String transactionId, String accountNumber, Long amount);

    void saveFailedCancelTransaction(String accountNumber, Long amount);

    TransactionDto queryTransacation(String transactionId);

}
