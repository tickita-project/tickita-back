package back.tickita.application.accountInfo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AccountInfoResponse {
    private Long accountId;
    private String image;
    private String email;
    private String nickName;
    private String phoneNumber;
}
