package back.tickita.application.account.service;

import back.tickita.application.account.dto.OAuth2UserInfo;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.token.entity.Token;
import back.tickita.domain.token.repository.TokenRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import back.tickita.security.oauth.AuthTokensGenerator;
import back.tickita.security.response.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@RequiredArgsConstructor
@Transactional
public class OauthService {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final AuthTokensGenerator authTokensGenerator;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String GOOGLE_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String GOOGLE_CLIENT_SECRET;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String GOOGLE_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.provider.google.token-uri}")
    private String GOOGLE_TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.google.user-info-uri}")
    private String GOOGLE_USER_INFO_URI;

    public TokenResponse refresh(String refreshToken) {
        Token token = tokenRepository.findByRefreshToken(refreshToken).orElseThrow(() -> new NotFoundException("리프레쉬 토큰이 존재하지않음"));
        return authTokensGenerator.generate(token.getAccount().getId(), LocalDateTime.now(), false);
    }

    @Transactional(noRollbackFor = TickitaException.class)
    public TokenResponse googleLogin(String code) {
        // 1. 인가 코드로 액세스 토큰 요청
        String accessToken = getGoogleAccessToken(code);

        // 2. 토큰으로 Google API 호출
        HashMap<String, Object> userInfo = getGoogleUserInfo(accessToken);

        // 3. Google ID로 회원가입 & 로그인 처리
        return googleUserLogin(userInfo);
    }

    // 1. 인가 코드로 액세스 토큰 요청
    private String getGoogleAccessToken(String code) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("code", code);
        body.add("client_id", GOOGLE_CLIENT_ID);
        body.add("client_secret", GOOGLE_CLIENT_SECRET);
        body.add("redirect_uri", GOOGLE_REDIRECT_URI);
        body.add("grant_type", "authorization_code");

        // HTTP 요청 전송
        HttpEntity<MultiValueMap<String, String>> googleTokenRequest = new HttpEntity(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<JsonNode> responseNode = restTemplate.exchange(
                GOOGLE_TOKEN_URI,
                HttpMethod.POST,
                googleTokenRequest,
                JsonNode.class);
        JsonNode accessTokenNode = responseNode.getBody();
        return accessTokenNode.get("access_token").asText();
    }

    // 2. 토큰으로 Google API 호출
    public HashMap<String, Object> getGoogleUserInfo(String accessToken) {
        // HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);

        // HTTP 요청 엔터티 생성
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            // Google API 호출
            ResponseEntity<HashMap> response = restTemplate.exchange(
                    GOOGLE_USER_INFO_URI,
                    HttpMethod.GET,
                    entity,
                    HashMap.class
            );
            // Google API 호출 응답 처리
            if (response.getStatusCode() != HttpStatus.OK) {
                throw new RuntimeException("Failed to get user info from Google API. Status code: " + response.getStatusCode());
            }
            return response.getBody();

        } catch (RestClientException e) {
            throw new RuntimeException("Failed to get user info from Google API", e);
        }
    }

    // 3. Google ID로 회원가입 & 로그인 처리
    public TokenResponse googleUserLogin(HashMap<String, Object> userInfo) {
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.ofGoogle(userInfo);
        String email = oAuth2UserInfo.email();
        Account account = accountRepository.findByEmail(email).orElseGet(() -> createNewAccount(oAuth2UserInfo));
        return authTokensGenerator.generate(account.getId(), LocalDateTime.now(), false);
    }

    private Account createNewAccount(OAuth2UserInfo oAuth2UserInfo) {
        Account newAccount = oAuth2UserInfo.toEntity();
        return accountRepository.save(newAccount);
    }
}
