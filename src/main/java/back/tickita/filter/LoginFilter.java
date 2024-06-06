package back.tickita.filter;

import back.tickita.application.account.JwtTokenProvider;
import back.tickita.security.entrypoint.TokenAuthenticationEntryPoint;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
public class LoginFilter extends OncePerRequestFilter {

    private final JwtTokenProvider tokenProvider;
    private final TokenAuthenticationEntryPoint tokenAuthenticationEntryPoint;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String tokenValue = tokenProvider.resolveToken(request);
        if (tokenValue != null) {
            String[] splitTokenValues = tokenValue.split(" ");
            if (splitTokenValues.length != 2 ||
                    !splitTokenValues[0].equals("Bearer") ||
                    !tokenProvider.validateToken(splitTokenValues[1])) {
                errorResponse(request, response, "인증이 안된 접근입니다.");
                return;
            }
            try {
                Authentication authentication = tokenProvider.getAuthentication(splitTokenValues[1],
                        LocalDateTime.now());
                if (authentication == null) {
                    errorResponse(request, response, "인증이 안된 접근입니다.");
                    return;
                }
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (Exception e) {
                if(e instanceof JwtException) {
                    errorResponse(request, response, e.getMessage());
                    return;
                }
            }
        }

        filterChain.doFilter(request, response);
    }
    private void errorResponse(HttpServletRequest request, HttpServletResponse response, String message)
            throws IOException, ServletException {
        tokenAuthenticationEntryPoint.commence(
                request,
                response,
                new AccountExpiredException(message)
        );
    }


}
