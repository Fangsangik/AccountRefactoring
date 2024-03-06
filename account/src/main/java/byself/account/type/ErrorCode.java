package byself.account.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    SOMETHING_WENT_WRONG("다시 시도하세요"),
    FIND_ID("해당 아이디를 찾았습니다."),
    SAME_USER_ACCOUNT("이미 아이디가 있습니다."),
    AMOUNT_EXCEED_BALANCE("잔액 초과"),
    CAN_NOT_FIND_USER_ID("해당 User 아이디를 찾을 수 없습니다"),
    INVALID_REQUEST("잘못된 접근");
    private final String description;
}
