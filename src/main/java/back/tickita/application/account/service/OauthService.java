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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;

import static back.tickita.domain.account.enums.SocialType.GOOGLE;
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

    private final HttpServletRequest httpServletRequest;

    public TokenResponse refresh(String refresh) {
        Token token = tokenRepository.findByRefresh(refresh).orElseThrow(() -> new TickitaException(TOKEN_NOT_FOUND));
        return authTokensGenerator.generate(token.getAccount().getId(), LocalDateTime.now(), false, Role.USER.name());
    }

    @Transactional(noRollbackFor = TickitaException.class)
    public TokenResponse kakaoLogin(String code, String currentDomain) {
        String redirectUri = selectRedirectUri(currentDomain);

        // 1. "인가 코드"로 "액세스 토큰" 요청
        String accessToken = getAccessToken(code, currentDomain);

        // 2. 토큰으로 카카오 API 호출
        HashMap<String, Object> userInfo= getKakaoUserInfo(accessToken);

        //3. 카카오ID로 회원가입 & 로그인 처리

        return kakaoUserLogin(userInfo);
    }

    public String selectRedirectUri(String currentDomain) {
        System.out.println(currentDomain);
        if (currentDomain.contains("localhost")){
            return "http://localhost:3000/sign-in/kakao";
        }else {
            return KAKAO_REDIRECT_URI;
        }
    }


    //1. "인가 코드"로 "액세스 토큰" 요청
    private String getAccessToken(String code, String redirectUri) {

        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();

        String requestURL = httpServletRequest.getRequestURI().toString();
        String originHeader = httpServletRequest.getHeader("Origin");
        String referer = httpServletRequest.getHeader("Referer");

        System.out.println("requestURL = " + requestURL);
        System.out.println("originHeader = " + originHeader);
        System.out.println("referer = " + referer);

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
        );


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

        if (jsonNode.has("error")) {
            throw new TickitaException(ErrorCode.INVALID_AUTH_CODE);
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
        return authTokensGenerator.generate(googleUser.getId(), LocalDateTime.now(), googleUser.isComplete(), String.valueOf(googleUser.getRole()));
    }

    private Account createNewAccount(HashMap<String, Object> userInfo) {
        Account newAccount = new Account();
        newAccount.setUserInfo(userInfo.get("email").toString(),GOOGLE);
        newAccount.setIsComplete(false);
        return accountRepository.save(newAccount);
    }
}
