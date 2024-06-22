package back.tickita.application.account.controller;

import back.tickita.application.account.service.OauthService;
import back.tickita.domain.token.repository.TokenRepository;
import back.tickita.security.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "소셜 로그인 API", description = "LoginController")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final OauthService oauthService;
    private final TokenRepository tokenRepository;

    @GetMapping("/login/oauth/kakao")
    @Operation(summary = "kakao 로그인", description = "로그인 한 회원의 정보를 등록합니다.")
    public ResponseEntity<TokenResponse> kakaoLogin(@RequestParam String code, @RequestParam String redirectUrl) {
        return ResponseEntity.ok(oauthService.kakaoLogin(code, redirectUrl));
    }

    @GetMapping("/login/oauth2/code/google")
    @Operation(summary = "Google 로그인", description = "로그인 한 회원의 정보를 등록합니다.")
    public ResponseEntity<TokenResponse> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(oauthService.googleLogin(code));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "kakao 토큰 재발급", description = "만료된 토큰을 재발급 합니다.")
    public TokenResponse setRefreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return oauthService.refresh(refreshTokenRequest.refresh());
    }

    @PostMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그인 한 회원의 토큰을 삭제합니다.")
    public ResponseEntity<String> logout(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        tokenRepository.deleteByRefresh(refreshTokenRequest.refresh());
        return ResponseEntity.ok("로그아웃 성공");
    }
}
