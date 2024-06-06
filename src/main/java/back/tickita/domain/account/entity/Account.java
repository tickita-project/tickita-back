package back.tickita.domain.account.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.account.enums.SocialType;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
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

    private boolean isComplete = false;

    public void setUserInfo(String email, SocialType socialType) {
        this.email = email;
        this.socialType = socialType;
    }

    public void setAccountInfo(String nickName, String phoneNumber, String image) {
        this.nickName = nickName;
        this.phoneNumber = phoneNumber;
        this.image = image;
    }

    public void setIsComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }
}
