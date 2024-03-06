package byself.account.service;

import byself.account.domain.Account;
import byself.account.domain.AccountUser;
import byself.account.exception.AccountException;
import byself.account.repository.AccountRepository;
import byself.account.repository.AccountUserRepository;
import byself.account.type.AccountStatus;
import byself.account.type.ErrorCode;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final AccountUserRepository accountUserRepository;

    @Transactional
    public Account getAccount(Long id) {
        if (id < 0) {
            throw new RuntimeException("Not Correct");
        }
        return accountRepository.findById(id).get();
    }

    private AccountUser getAccountUser(Long id) {
        return accountUserRepository.findById(id)
                .orElseThrow(() -> new AccountException(ErrorCode.CAN_NOT_FIND_USER_ID));
        //AccountException 상속 해주는 부분이 잘못 되었음,
        //Exception을 상속하면, 예외 처리 해주는 부분을 달고 다녀야 함.
    }

    private void validateAccountUser(AccountUser accountUser){
        if (accountRepository.alreadyCreatedUser(accountUser)== 1L){
            throw new AccountException(ErrorCode.SAME_USER_ACCOUNT);
        }

    }
}
