package back.tickita.handler;

import back.tickita.application.token.JwtTokenProvider;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Component
@AllArgsConstructor
public class JwtAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, @NonNull HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        // JWT 토큰 생성 및 직렬화
        String tokenJson = jwtTokenProvider.generateTokens(authentication);

        // 응답
        response.setContentType("application/json");
        response.getWriter().write(tokenJson);

    }
}