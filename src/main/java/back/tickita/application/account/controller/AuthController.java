package back.tickita.application.account.controller;

import back.tickita.application.token.JwtTokenProvider;
import back.tickita.security.oauth.JwtToken;
import back.tickita.security.oauth.TokenRefreshRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@AllArgsConstructor
public class AuthController {

    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/loginSuccess")
    public void loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User, HttpServletResponse response) throws IOException {
        response.sendRedirect("/home");
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal OAuth2User oAuth2User) {
        return "Welcome, " + oAuth2User.getAttribute("name");
    }

    @PostMapping("/refreshToken")
    public ResponseEntity<?> refreshToken(@RequestBody TokenRefreshRequest request) throws JsonProcessingException {

        String expiredAccessToken = request.getExpiredAccessToken();
        String refreshToken = request.getRefreshToken();

        // 리프레시 토큰을 검증하고 새로운 액세스 토큰과 리프레스 토큰을 생성
        JwtToken newJwtToken = jwtTokenProvider.refreshAccessToken(expiredAccessToken, refreshToken);

        if (newJwtToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Refresh token is invalid or expired.");
        }

        Map<String, Object> jwtToken = new HashMap<>();
        jwtToken.put("accessToken", newJwtToken.getAccessToken());
        jwtToken.put("refreshType", newJwtToken.getRefreshToken());
        jwtToken.put("grantType", newJwtToken.getGrantType());

        Map<String, Object> wrappedToken = new HashMap<>();
        wrappedToken.put("token", jwtToken);

        return ResponseEntity.ok(wrappedToken);
    }
}