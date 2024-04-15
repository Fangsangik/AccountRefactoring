package byself.account.exception;

import byself.account.dto.ErrorResponse;
import byself.account.type.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static byself.account.type.ErrorCode.*;
import static byself.account.type.ErrorCode.INVALID_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleAccountExcpetion(AccountException e) {
        log.error("{} is occured", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArugmentNotValidException(AccountException e){
        log.error("handleMethodArugmentNotValidException has been occured");

        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescrpition());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse dataIntegrityViolationException (AccountException e){
        log.error("dataIntegrityViolationException has been occured");

        return new ErrorResponse(INVALID_REQUEST, INVALID_REQUEST.getDescrpition());
    }

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handleException(Exception e){
        log.error("Exception", e);

        return new ErrorResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR.getDescrpition());
    }
}
