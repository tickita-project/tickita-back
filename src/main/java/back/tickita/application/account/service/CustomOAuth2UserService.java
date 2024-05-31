package back.tickita.application.account.service;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.enums.SocialType;
import back.tickita.application.account.repository.AccountRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final AccountRepository accountRepository;
    private final HttpSession httpSession;

    @Autowired
    public CustomOAuth2UserService(AccountRepository accountRepository, HttpSession httpSession) {
        this.accountRepository = accountRepository;
        this.httpSession = httpSession;
    }

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

//        String registrationId = userRequest.getClientRegistration().getRegistrationId();
//        String userNameAttributeName = userRequest.getClientRegistration()
//                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

//        String email = getEmail(oAuth2User, registrationId);
        String email = oAuth2User.getAttribute("email");
        if (email == null) {
            throw new OAuth2AuthenticationException("Email not found in OAuth2 user attributes");
        }

        Account account = accountRepository.findByEmail(email)
                .orElseGet(() -> createNewAccount(oAuth2User, email));

        httpSession.setAttribute("account", account);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                oAuth2User.getAttributes(),
                "email"
        );
    }

//    private String getEmail(OAuth2User oAuth2User, String registrationId) {
//        if ("google".equals(registrationId)) {
//            return oAuth2User.getAttribute("email");
//        } else {
//            throw new OAuth2AuthenticationException("허용되지 않는 인증입니다");
//        }
//    }

    private Account createNewAccount(OAuth2User oAuth2User, String email) {
        Account account = new Account();
        account.setEmail(email);
        account.setNickName(oAuth2User.getAttribute("name"));
        account.setImage(oAuth2User.getAttribute("picture"));
        account.setSocialType(SocialType.GOOGLE);
        return accountRepository.save(account);
    }
}
