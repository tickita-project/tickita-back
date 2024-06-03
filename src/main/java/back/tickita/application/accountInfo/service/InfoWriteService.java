package back.tickita.application.accountInfo.service;

import back.tickita.application.accountInfo.dto.ImageRequest;
import back.tickita.application.accountInfo.dto.request.AccountInfoRequest;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
@Transactional
public class InfoWriteService {

    private final AccountRepository accountRepository;
    private final ImageUploadService imageUploadService;

    public String updateAccountInfo(MultipartFile multipartFile,AccountInfoRequest accountRequest) throws IOException {
        Account account = accountRepository.findById(accountRequest.getAccountId()).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setAddInfoCompleted(true);
        account.setAccountInfo(accountRequest.getNickName(), accountRequest.getPhoneNumber(), imageUploadService.uploadImage(multipartFile));
        return "성공";
    }
}
