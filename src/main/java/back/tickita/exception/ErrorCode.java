package back.tickita.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "신규 회원 입니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    TOKEN_EXPIRE(HttpStatus.BAD_REQUEST, "토큰 시간이 만료되었습니다.");

    private final HttpStatus httpStatus;
    private final String message;
}
