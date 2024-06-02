package back.tickita.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class TickitaException extends RuntimeException{

    private final ErrorCode errorCode;
}
