package back.tickita.application.accountInfo.service;

import back.tickita.application.accountInfo.dto.request.AccountInfoRequest;
import back.tickita.application.accountInfo.dto.response.AccountImgUrlResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.token.entity.Token;
import back.tickita.domain.token.repository.TokenRepository;
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
    private final TokenRepository tokenRepository;

    public TokenResponse updateAccountInfo(AccountInfoRequest accountRequest, String role){
        Account account = accountRepository.findById(accountRequest.getAccountId()).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        account.setIsComplete(true);
        account.setAccountInfo(accountRequest.getNickName(), accountRequest.getPhoneNumber(), accountRequest.getImgUrl());
        return authTokensGenerator.generate(account.getId(), LocalDateTime.now(), true, role);
    }

    public AccountImgUrlResponse updateAccountImg(MultipartFile multipartFile) throws IOException {
        return new AccountImgUrlResponse(imageUploadService.uploadImage(multipartFile));
    }

    public String accountWithdrawal(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        Token token = tokenRepository.findByAccountId(accountId).orElse(null);
        tokenRepository.delete(token);
        accountRepository.delete(account);
        return "회원 탈퇴 성공";
    }
}
