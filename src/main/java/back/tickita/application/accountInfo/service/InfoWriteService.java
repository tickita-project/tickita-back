package back.tickita.application.accountInfo.service;

import back.tickita.application.accountInfo.dto.request.AccountInfoRequest;
import back.tickita.application.accountInfo.dto.response.AccountImgUrlResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import back.tickita.security.oauth.AuthTokensGenerator;
import back.tickita.security.response.TokenResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class InfoWriteService {

    private final AccountRepository accountRepository;
    private final ImageUploadService imageUploadService;
    private final AuthTokensGenerator authTokensGenerator;

    public TokenResponse updateAccountInfo(AccountInfoRequest accountRequest) {
        Account account = accountRepository.findById(accountRequest.getAccountId()).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setIsComplete(true);
        account.setAccountInfo(accountRequest.getNickName(), accountRequest.getPhoneNumber(), accountRequest.getImgUrl());
        return authTokensGenerator.generate(account.getId(), LocalDateTime.now(), true);
    }

    public AccountImgUrlResponse updateAccountImg(MultipartFile multipartFile) throws IOException {
        String imgUrl = imageUploadService.uploadImage(multipartFile);
        return new AccountImgUrlResponse(imgUrl);
    }
}
