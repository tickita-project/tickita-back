package back.tickita.interceptor;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import back.tickita.interceptor.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerMethodArgumentResolver {

    private final AccountRepository accountRepository;
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String[] roles = authentication.getAuthorities().stream().findFirst().toString().split("_");
        String authRole = roles[1];
        authRole = authRole.replaceAll("]", "");
        if (authRole.equalsIgnoreCase("ANONYMOUS")) {
            throw new TickitaException(ErrorCode.FORBIDDEN_ACCESS);
        }
        String email = authentication.getName();
        Account account = accountRepository.findByEmail(email
        ).orElseThrow(() -> new TickitaException(ErrorCode.TOKEN_EXPIRE));
        return new LoginUserInfo(account.getId());
    }
}
