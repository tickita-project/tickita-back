package back.tickita.application.account.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public class CommonResponse<T>{
    private final int code;
    private final HttpStatus status;
    private T data;

    private CommonResponse(HttpStatus status, T data) {
        this.code = status.value();
        this.status = status;
        this.data = data;
    }

    public static <T>CommonResponse<T> of(HttpStatus status, T data) {
        return new CommonResponse<>(status, data);
    }

    public static <T> CommonResponse<T> badRequest(T errorData) {
        return of(HttpStatus.BAD_REQUEST, errorData);
    }

    public static <T> CommonResponse<T> ok(T data) {
        return of(HttpStatus.OK, data);
    }
    public static <T>CommonResponse<T> serverError(T data) {
        return of(HttpStatus.INTERNAL_SERVER_ERROR, data);
    }
}
