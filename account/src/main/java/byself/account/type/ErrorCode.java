package byself.account.type;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_ENOUGH_MONEY("잔액 부족!"),
    INVALID_REQUEST("잘못된 요청입니다"),
    USER_NOT_FOUND("사용자를 찾지 못했습니다."),
    ACCOUNT_NOT_FOUND("계좌를 찾지 못했습니다."),
    ID_NOT_MATCH("아이디가 맞지 않습니다."),
    ALREADY_UNREGISTERED("이미 탈퇴한 회원 입니다"),
    BALANCE_LEFT_MONEY("잔액이 남아있습니다."),
    MAX_ACCOUNT_PER_USER("한 사람당 만들수 있는 계좌는 2개 입니다."),
    AMOUNT_EXCEED("금액이 초과되었습니다."),
    TOO_OLD_TO_CNACEL("오래된 계좌 입니다."),
    TRANSACTION_NOT_FOUND("트랜젝션을 찾지 못했습니다."),
    ACCOUNT_TRANSACTION_LOCK("해당 계좌는 사용중");
    private final String descrpition;
}
