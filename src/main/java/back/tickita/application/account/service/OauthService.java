package back.tickita.application.account.service;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.token.entity.Token;
import back.tickita.domain.token.repository.TokenRepository;
import back.tickita.exception.TickitaException;
import back.tickita.security.oauth.AuthTokensGenerator;
import back.tickita.security.response.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.webjars.NotFoundException;
import java.time.LocalDateTime;
import java.util.HashMap;
import static back.tickita.domain.account.enums.SocialType.GOOGLE;

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

    private final String GOOGLE_TOKEN_URI = "https://oauth2.googleapis.com/token";

    private final String GOOGLE_USER_INFO_URI = "https://www.googleapis.com/oauth2/v2/userinfo";

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
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

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
        ResponseEntity<String> response = restTemplate.exchange(
                GOOGLE_TOKEN_URI,
                HttpMethod.POST,
                googleTokenRequest,
                String.class);
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonNode.get("access_token").asText();
    }

    // 2. 토큰으로 Google API 호출
    public HashMap<String, Object> getGoogleUserInfo(String accessToken) {
        HashMap<String, Object> userInfo= new HashMap<String,Object>();

        // HTTP Header 설정
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 엔터티 생성
        HttpEntity<MultiValueMap<String, String>> googleUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        // Google API 호출
        ResponseEntity<String> response = restTemplate.exchange(
                GOOGLE_USER_INFO_URI,
                HttpMethod.GET,
                googleUserInfoRequest,
                String.class
        );
        // responseBody에 있는 정보를 꺼냄
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        Long id = jsonNode.get("id").asLong();
        String email = jsonNode.get("email").asText();
        userInfo.put("id",id);
        userInfo.put("email",email);
        return userInfo;
    }

    // 3. Google ID로 회원가입 & 로그인 처리
    public TokenResponse googleUserLogin(HashMap<String, Object> userInfo) {
        String email = userInfo.get("email").toString();
        Account googleUser = accountRepository.findByEmail(email).orElseGet(() -> createNewAccount(userInfo));
        if (!googleUser.isComplete()) {
            return new TokenResponse(googleUser.getId(), null, null, null, null, null, false,
                    googleUser.getEmail(), null, null, null);
        }
        return authTokensGenerator.generate(googleUser.getId(), LocalDateTime.now(), false);
    }

    private Account createNewAccount(HashMap<String, Object> userInfo) {
        Account newAccount = new Account();
        newAccount.setUserInfo(userInfo.get("email").toString(),GOOGLE);
        newAccount.setIsComplete(false);
        return accountRepository.save(newAccount);
    }
}
