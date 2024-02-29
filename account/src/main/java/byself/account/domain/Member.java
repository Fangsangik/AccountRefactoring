package byself.account.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@RequiredArgsConstructor
public class Member {
    private String accountNumber;
    private Long id;
    private Long balance;
    private String password;
    private LocalDateTime registerdDate;
    private LocalDateTime unRegisterDate;
}
