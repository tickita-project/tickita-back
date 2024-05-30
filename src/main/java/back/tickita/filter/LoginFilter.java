package back.tickita.filter;

import back.tickita.application.account.JwtTokenProvider;
import back.tickita.domain.token.repository.TokenRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final TokenRepository tokenRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null) {
            String[] loginTokens = token.split(" ");
            if (loginTokens.length != 2 || token.toLowerCase().startsWith("bearer ") ||
                    jwtTokenProvider.validateToken(loginTokens[1]) ||
                    tokenRepository.existsByAccess(loginTokens[1])
            ) {
                return;
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(loginTokens[1]);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(request,response);
        }

    }

}
