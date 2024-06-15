package back.tickita.application.account.service;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.entity.enums.Role;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.token.entity.Token;
import back.tickita.domain.token.repository.TokenRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import back.tickita.security.oauth.AuthTokensGenerator;
import back.tickita.security.response.TokenResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.FactoryBean;
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

import static back.tickita.domain.account.enums.SocialType.KAKAO;
import static back.tickita.exception.ErrorCode.TOKEN_NOT_FOUND;


@Service
@RequiredArgsConstructor
@Transactional
public class OauthService {

    private final AccountRepository accountRepository;
    private final TokenRepository tokenRepository;
    private final AuthTokensGenerator authTokensGenerator;

    @Value("${spring.security.oauth2.client.registration.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.redirect-uri}")
    private String KAKAO_REDIRECT_URI;

    @Value("${spring.security.oauth2.client.registration.client-secret}")
    private String KAKAO_SECRET_ID;

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String KAKAO_TOKEN_URI;

    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String KAKAO_USER_INFO_URI;

    private final HttpServletRequest httpServletRequest;

    public TokenResponse refresh(String refresh) {
        Token token = tokenRepository.findByRefresh(refresh).orElseThrow(() -> new TickitaException(TOKEN_NOT_FOUND));
        return authTokensGenerator.generate(token.getAccount().getId(), LocalDateTime.now(), false, Role.USER.name());
    }

    @Transactional(noRollbackFor = TickitaException.class)
    public TokenResponse kakaoLogin(String code) {
        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code);

        // 2. 토큰으로 카카오 API 호출
        HashMap<String, Object> userInfo= getKakaoUserInfo(accessToken);

        //3. 카카오ID로 회원가입 & 로그인 처리

        return kakaoUserLogin(userInfo);
    }

    //1. "인가 코드"로 "액세스 토큰" 요청
    private String getAccessToken(String code) {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        String requestURL = httpServletRequest.getRequestURL().toString();
        if (requestURL.contains("localhost:3000")){
            KAKAO_REDIRECT_URI = "http://localhost:3000/sign-in/kakao";
        } else {
            KAKAO_REDIRECT_URI = "https://tickita.net/sign-in/kakao";
        }
        body.add("grant_type", "authorization_code");
        body.add("client_id", KAKAO_CLIENT_ID);
        body.add("redirect_uri", KAKAO_REDIRECT_URI);
        body.add("client_secret", KAKAO_SECRET_ID);
        body.add("code", code);
        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> response =  rt.exchange(
                KAKAO_TOKEN_URI,
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );;


        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = null;
        try {
            jsonNode = objectMapper.readTree(responseBody);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new TickitaException(ErrorCode.KAKAO_API_ERROR);
        }

        if (jsonNode.has("error")) {
            throw new TickitaException(ErrorCode.INVALID_AUTH_CODE);
        }

        return jsonNode.get("access_token").asText(); //토큰 전송
    }

    //2. 토큰으로 카카오 API 호출
    private HashMap<String, Object> getKakaoUserInfo(String accessToken) {
        HashMap<String, Object> userInfo= new HashMap<String,Object>();

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoUserInfoRequest = new HttpEntity<>(headers);
        RestTemplate rt = new RestTemplate();
        rt.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        ResponseEntity<String> response = rt.exchange(
                KAKAO_USER_INFO_URI,
                HttpMethod.POST,
                kakaoUserInfoRequest,
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
        String email = jsonNode.get("kakao_account").get("email").asText();
        userInfo.put("id",id);
        userInfo.put("email",email);
        return userInfo;
    }

    //3. 카카오ID로 회원가입 & 로그인 처리

    public TokenResponse kakaoUserLogin(HashMap<String, Object> userInfo){

        String kakaoEmail = userInfo.get("email").toString();

        Account kakaoUser = accountRepository.findByEmail(kakaoEmail).orElse(null);

        if (kakaoUser == null) {//회원가입
            kakaoUser = new Account();
            kakaoUser.setUserInfo(kakaoEmail,KAKAO);
            kakaoUser.setIsComplete(false);
            kakaoUser = accountRepository.save(kakaoUser);
        }
        if (!kakaoUser.isComplete()){
            return new TokenResponse(kakaoUser.getId(), null, null, null, null, null, false,
                    kakaoUser.getEmail(), null, null, null);
        }
            return authTokensGenerator.generate(kakaoUser.getId(), LocalDateTime.now(), kakaoUser.isComplete(), String.valueOf(kakaoUser.getRole()));
    }
}
