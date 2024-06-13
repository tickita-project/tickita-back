package back.tickita.domain.account.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.account.entity.enums.Role;
import back.tickita.domain.account.enums.SocialType;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.token.entity.Token;
import io.jsonwebtoken.Jwts;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

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

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<CrewList> crewList;

    @OneToOne(mappedBy = "account", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Token token;

    public void setUserInfo(String email, SocialType socialType) {
        this.email = email;
        this.socialType = socialType;
        role = Role.USER;
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
