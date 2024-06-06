package back.tickita.application.account.controller;
import back.tickita.application.account.service.OauthService;
import lombok.RequiredArgsConstructor;
import back.tickita.security.response.TokenResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "소셜 로그인 API", description = "LoginController")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final OauthService oauthService;

    @GetMapping("/login/oauth2/code/google")
    @Operation(summary = "Google 로그인", description = "로그인 한 회원의 정보를 등록합니다.")
    public ResponseEntity<TokenResponse> googleLogin(@RequestParam String code) {
        return ResponseEntity.ok(oauthService.googleLogin(code));
    }

    @PostMapping("/token/refresh")
    @Operation(summary = "토큰 재발급", description = "만료된 토큰을 재발급 합니다.")
    public TokenResponse setRefreshToken(@RequestBody String refreshToken) {
        return oauthService.refresh(refreshToken);
    }
}
