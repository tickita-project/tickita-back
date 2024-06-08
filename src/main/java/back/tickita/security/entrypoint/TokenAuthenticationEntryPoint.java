package back.tickita.security.entrypoint;

import back.tickita.application.account.dto.response.CommonResponse;
import back.tickita.domain.account.repository.log.ErrorLog;
import back.tickita.domain.account.repository.log.ErrorLogRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class TokenAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;
    private final ErrorLogRepository errorLogRepository;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding("UTF-8");
//        response.setStatus(401);
//        response.getWriter().write("인증되지 않은 접근입니다.");
        errorLogRepository.save(ErrorLog.create(
                401, request.getMethod(), "UNAUTHORIZED", authException.getMessage(),
                request.getRequestURI().toString()
        ));
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(401);
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        CommonResponse.of(HttpStatus.UNAUTHORIZED, authException.getMessage()))
        );
    }
}
