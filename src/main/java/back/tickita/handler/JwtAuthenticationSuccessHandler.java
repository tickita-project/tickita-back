package back.tickita.handler;

import back.tickita.config.jwt.JwtTokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//@Component
//public class OAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {
//
//    @Autowired
//    private JwtTokenProvider tokenProvider;
//
//    @Autowired
//    private AccountRepository accountRepository;
//
//    @Override
//    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//                                        Authentication authentication) throws IOException {
//
//        String email = ((DefaultOAuth2User) authentication.getPrincipal()).getAttribute("email");
//
//        Optional<Account> accountOptional = accountRepository.findByEmail(email);
//        if (accountOptional.isPresent()) {
//            Account account = accountOptional.get();
//            UserDetails userDetails = new org.springframework.security.core.userdetails.User(account.getEmail(),
//                    "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
//            String token = tokenProvider.generateToken(authentication);
//            response.setHeader("Authorization", "Bearer " + token);
//        }
//
//        response.sendRedirect("/");
//    }
//}

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