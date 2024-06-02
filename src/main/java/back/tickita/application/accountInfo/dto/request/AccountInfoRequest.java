package back.tickita.application.accountInfo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class AccountInfoRequest {
    private MultipartFile imgUrl;
    private String nickName;
    private String phoneNumber;
}
