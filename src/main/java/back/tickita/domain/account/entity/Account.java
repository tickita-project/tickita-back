package back.tickita.domain.account.entity;

import back.tickita.common.BaseEntity;
import back.tickita.domain.account.enums.Role;
import back.tickita.domain.account.enums.SocialType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}