package back.tickita.filter.handler;

import back.tickita.application.account.annotaion.LoginUser;
import back.tickita.application.account.dto.response.LoginUserInfo;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.exception.TickitaException;
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
public class LoginInterception implements HandlerMethodArgumentResolver {

    private final AccountRepository accountRepository;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(LoginUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String[] roles = authentication.getAuthorities().stream().findFirst().toString().split("_");
        String authRole = roles[1];
        authRole = authRole.replaceAll("]","");

        if(authRole.equalsIgnoreCase("ANONYMOUS")) {
            throw new TickitaException("접근 권한이 없습니다.");
        }
        Account account = accountRepository.findByEmail(
                authentication.getName()).orElseThrow(() -> new TickitaException("토큰시간이 만료되었습니다."));
        return new LoginUserInfo(account.getId());
    }
}
