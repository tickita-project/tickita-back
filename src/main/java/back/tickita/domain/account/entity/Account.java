package back.tickita.domain.account.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.account.enums.SocialType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Account extends BaseEntity {

    @Column(nullable = false)
    private String email;

    private String nickName;

    private String phoneNumber;

    private String image;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    public void setUserInfo(String email, SocialType socialType) {
        this.email = email;
        this.socialType = socialType;
    }
}

