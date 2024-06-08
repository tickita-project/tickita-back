package back.tickita.domain.account.entity.enums;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Role {

    USER("회원");
    private final String content;
}
