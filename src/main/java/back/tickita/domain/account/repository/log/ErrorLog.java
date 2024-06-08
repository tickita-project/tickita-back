package back.tickita.domain.account.repository.log;

import back.tickita.common.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorLog extends BaseEntity {

    @Column(nullable = false)
    private int statusCode;
    @Column(nullable = false)
    private String httpMethod;
    @Column(nullable = false)
    private String status;
    @Column(nullable = false)
    private String errorMessage;
    @Column(nullable = false)
    private String requestPath;

    private ErrorLog(int statusCode, String httpMethod, String status, String errorMessage,
                     String requestPath) {
        this.statusCode = statusCode;
        this.httpMethod = httpMethod;
        this.status = status;
        this.errorMessage = errorMessage;
        this.requestPath = requestPath;
    }

    public static ErrorLog create(int statusCode, String httpMethod, String status, String errorMessage,
                                  String requestPath) {
        return new ErrorLog(statusCode, httpMethod,status, errorMessage, requestPath);
    }
}
