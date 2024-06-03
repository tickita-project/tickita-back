package back.tickita.application.account.service;

import back.tickita.application.account.dto.OAuth2UserInfo;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;

    @Transactional
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        Map<String, Object> oAuth2UserAttributes = super.loadUser(userRequest).getAttributes();

        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfo.ofGoogle(oAuth2UserAttributes);

        Account account = accountRepository.findByEmail(oAuth2UserInfo.email())
                .orElseGet(() -> createNewAccount(oAuth2UserInfo));

        return new PrincipalDetails(account, oAuth2UserAttributes);
    }

    private Account createNewAccount(OAuth2UserInfo oAuth2UserInfo) {
        Account newAccount = oAuth2UserInfo.toEntity();
        return accountRepository.save(newAccount);
    }
}

