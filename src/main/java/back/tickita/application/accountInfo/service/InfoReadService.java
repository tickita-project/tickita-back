package back.tickita.application.accountInfo.service;

import back.tickita.application.accountInfo.dto.response.AccountImgUrlResponse;
import back.tickita.application.accountInfo.dto.response.AccountInfoResponse;
import back.tickita.application.accountInfo.dto.response.AccountResponse;
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
@Transactional(readOnly = true)
public class InfoReadService {

    private final AccountRepository accountRepository;
    private final ImageUploadService imageUploadService;

    public AccountInfoResponse findAccountInfo(Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        return new AccountInfoResponse(account.getId(), account.getImage(), account.getEmail(), account.getNickName(), account.getPhoneNumber());
    }

    public AccountResponse findAccountEmail(Long accountId){
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        return new AccountResponse(account.getEmail());
    }

    public AccountImgUrlResponse updateAccountImg(MultipartFile multipartFile) throws IOException {
        String imgUrl = imageUploadService.uploadImage(multipartFile);
        return new AccountImgUrlResponse(imgUrl);
    }
}
