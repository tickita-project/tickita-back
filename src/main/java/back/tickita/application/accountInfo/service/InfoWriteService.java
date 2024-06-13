package back.tickita.application.accountInfo.service;

import back.tickita.application.accountInfo.dto.request.AccountInfoRequest;
import back.tickita.application.accountInfo.dto.response.AccountImgUrlResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.notification.entity.CrewNotification;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.repository.CrewNotificationRepository;
import back.tickita.domain.notification.repository.NotificationRepository;
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
    private final CrewListRepository crewListRepository;
    private final NotificationRepository notificationRepository;
    private final CrewNotificationRepository crewNotificationRepository;

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
        CrewList crewList = crewListRepository.findByAccount_Id(account.getId()).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        CrewNotification crewNotification = crewNotificationRepository.findByCrewList_Account_Id(account.getId()).orElseThrow(() -> new TickitaException(ErrorCode.NOTIFICATION_NOT_FOUND));
        tokenRepository.delete(token);
        crewListRepository.delete(crewList);
        crewNotificationRepository.delete(crewNotification);
        accountRepository.delete(account);
        return "회원 탈퇴 성공";
    }
}
