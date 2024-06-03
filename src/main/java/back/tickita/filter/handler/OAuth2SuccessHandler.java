package back.tickita.filter.handler;

import back.tickita.application.account.service.OauthService;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.account.entity.Account;
import back.tickita.security.oauth.AuthTokensGenerator;
import back.tickita.security.response.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final AuthTokensGenerator authTokensGenerator;
    private final AccountRepository accountRepository;
    private final OauthService oauthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // 사용자의 이메일 가져오기
        Account account = accountRepository.findByEmail(authentication.getName()).orElse(null);

        // 액세스 토큰 및 리프레시 토큰 생성
        TokenResponse token = authTokensGenerator.generate(account.getId(), LocalDateTime.now());

        // ObjectMapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();

        // JSR310 모듈 등록
        objectMapper.registerModule(new JavaTimeModule());

        // AuthTokens 객체를 JSON으로 변환
        String authTokensJson = objectMapper.writeValueAsString(token);

        // JSON 형태의 문자열을 응답으로 전송
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(authTokensJson);

        System.out.println("OAuth2 인증 성공");
    }
}