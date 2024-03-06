package byself.account.exception;

import byself.account.type.ErrorCode;
import ch.qos.logback.core.spi.ErrorCodes;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountException extends RuntimeException {

    private ErrorCode errorCode;
    private String getErrorMessage;

    public AccountException (ErrorCode errorCode){
        this.errorCode = errorCode;
        this.getErrorMessage = errorCode.getDescription();
    }

}
