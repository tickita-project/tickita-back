package back.tickita.domain.token;

import back.tickita.domain.account.entity.Account;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String email;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Instant expiryDate;

//    @TimeToLive
//    private Long expiration;
//
//    public static RefreshToken from(String username, String refreshToken, Long expirationTime) {
//        return RefreshToken.builder()
//                .id(username)
//                .refreshToken(refreshToken)
//                .expiration(expirationTime / 1000)
//                .build();
//    }
}
