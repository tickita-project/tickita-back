package back.tickita.application.account.controller;

import back.tickita.application.account.service.OauthService;
import back.tickita.security.oauth.AuthTokensGenerator;

import back.tickita.security.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "소셜 로그인 API", description = "LoginController")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final OauthService oauthService;

    @GetMapping("/login/oauth/kakao")
    @Operation(summary = "kakao 로그인", description = "로그인 한 회원의 정보를 등록합니다.")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestParam String code) {
        return ResponseEntity.ok(oauthService.kakaoLogin(code));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "kakao 토큰 재발급", description = "만료된 토큰을 재발급 합니다.")
    public TokenResponse setRefreshToken(@RequestBody String refreshToken, LocalDateTime now) {
        return oauthService.refresh(refreshToken);
    }
}
