package back.tickita.application.accountInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
public class ImageRequest {
    private MultipartFile imgUrl;
}
