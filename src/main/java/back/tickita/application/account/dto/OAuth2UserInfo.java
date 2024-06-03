package back.tickita.application.account.dto;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.enums.Role;
import back.tickita.domain.account.enums.SocialType;
import lombok.Builder;

import java.util.Map;

@Builder
public record OAuth2UserInfo(String email, SocialType socialType) {
    public static OAuth2UserInfo ofGoogle(Map<String, Object> attributes) {
        return OAuth2UserInfo.builder()
                .email((String) attributes.get("email"))
                .build();
    }

    public Account toEntity() {
        return Account.builder()
                .email(email)
                .socialType(SocialType.GOOGLE)
                .role(Role.USER)
                .build();
    }
}
