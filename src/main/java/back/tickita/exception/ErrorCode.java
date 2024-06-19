package back.tickita.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "회원을 찾을 수 없습니다."),
    FORBIDDEN_ACCESS(HttpStatus.FORBIDDEN, "접근 권한이 없습니다."),
    TOKEN_EXPIRE(HttpStatus.BAD_REQUEST, "토큰 시간이 만료되었습니다."),

    TOKEN_NOT_FOUND(HttpStatus.BAD_REQUEST, "리프레시 토큰을 찾을 수 없습니다."),

    INVALID_AUTH_CODE(HttpStatus.BAD_REQUEST, "유효하지 않은 인가 코드입니다."),
    DUPLICATE_AUTH_CODE(HttpStatus.BAD_REQUEST, "중복된 인가 코드입니다."),
    KAKAO_API_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "카카오 API 호출 중 오류가 발생했습니다."),
    UNABLE_TO_SEND_EMAIL(HttpStatus.BAD_REQUEST, "메일을 보낼 수 없습니다."),
    CREW_NOT_FOUND(HttpStatus.BAD_REQUEST, "그룹을 찾을 수 없습니다."),
    INVITATION_ALREADY_SENT(HttpStatus.BAD_REQUEST, "이미 초대 된 회원입니다."),
    INVITATION_ALREADY_ACCEPTED(HttpStatus.BAD_REQUEST, "이미 그룹에 가입된 회원입니다."),
    INVITATION_INVALID(HttpStatus.BAD_REQUEST, "잘못 초대 하였습니다."),
    INVITATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "그룹 상태값이 다릅니다."),
    NOTIFICATION_NOT_FOUND(HttpStatus.BAD_REQUEST, "알림을 찾을 수 없습니다."),
    VOTE_SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "투표를 찾을 수 없습니다.")
    ;

    private final HttpStatus httpStatus;
    private final String message;
}
