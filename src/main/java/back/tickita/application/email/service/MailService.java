package back.tickita.application.email.service;

import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.enums.CrewRole;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.crews.repository.CrewsRepository;
import back.tickita.domain.notification.entity.CrewNotification;
import back.tickita.domain.notification.entity.Notification;
import back.tickita.domain.notification.eums.NotificationType;
import back.tickita.domain.notification.repository.CrewNotificationRepository;
import back.tickita.domain.notification.repository.NotificationRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;



@Slf4j
@Service
@RequiredArgsConstructor
@Component
public class MailService {

    private final JavaMailSender emailSender;
    private final AccountRepository accountRepository;
    private final CrewsRepository crewsRepository;
    private final CrewListRepository crewListRepository;
    private final NotificationRepository notificationRepository;
    private final CrewNotificationRepository crewNotificationRepository;

    @Value("${spring.mail.username}")
    private String userEmail;

    /** 이메일 전송 **/
    public void sendMail(String email, String crewName, String nickName) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(email);
            helper.setFrom(userEmail);
            helper.setSubject(crewName + " 그룹에서 초대가 왔습니다.");

            String htmlContent = "<html>" +
                    "<body>" +
                    "<table style=\"width: 1400px; height: 700px; background-color: #f0f6ff;\">" +
                    "<tbody>" +
                    "<tr>" +
                    "<td>" +
                    "<table style=\"width: 700px; height: 500px; padding: 30px; margin: 0 auto; border: 1px solid #dddddd; border-top: 5px solid #3360ff; background-color: #ffffff; color: #000000;\">" +
                    "<thead>" +
                    "<tr>" +
                    "<td style=\"display: flex;\">" +
                    "<img style=\"width: 132px; height: 44px; margin-top: 30px;\" src=\"https://tickita-bucket.s3.ap-northeast-2.amazonaws.com/06faa78c-5381-465f-ad0b-9e7bc3705bdd\" alt=\"티키타 기본 로고\" />" +
                    "<img style=\"width: 100px; height: 104px; margin-left: 500px;\" src=\"https://tickita-bucket.s3.ap-northeast-2.amazonaws.com/acd55cfb-6170-4f71-ae09-4ebbfec69a93\" alt=\"티키타 애니메이션 로고\" />" +
                    "</td>" +
                    "</tr>" +
                    "</thead>" +
                    "<tbody style=\"font-size: 24px; font-weight: 500;\">" +
                    "<tr>" +
                    "<td style=\"padding-top: 30px; font-size: 28px; font-weight: 700; padding-bottom: 15px; border-bottom: 3px solid #000000;\">그룹 초대 알림</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"padding-top: 40px;\">안녕하세요 " + nickName + " 님,</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"padding-top: 15px;\">" + crewName +" 그룹에 초대되셨습니다.</td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td style=\"padding-top: 15px;\">초대 수락은 대시보드 알림을 확인해 주세요!</td>" +
                    "</tr>" +
                    "<tr style=\"text-align: center;\">" +
                    "<td style=\"margin-top: 50px; width: 300px; display: inline-block; background-color: #3360ff; border-radius: 15px;\">" +
                    "<a href=\"https://tickita.net/dashboard\" style=\"color: #ffffff; text-decoration: none; display: inline-block; padding: 25px; font-size: 22px;\">대시보드 바로가기</a>" +
                    "</td>" +
                    "</tr>" +
                    "</tbody>" +
                    "</table>" +
                    "</td>" +
                    "</tr>" +
                    "</tbody>" +
                    "</table>" +
                    "</body>" +
                    "</html>";


            helper.setText(htmlContent, true);

            emailSender.send(message);
            log.info("HTML 메일 전송 완료");

        } catch (Exception e) {
            log.error("HTML 메일 전송 실패", e);
            throw new TickitaException(ErrorCode.UNABLE_TO_SEND_EMAIL);
        }
    }

    public String setInviteCrew(Long accountId, String email, Long crewId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        Account invite = accountRepository.findByEmail(email).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        Crews crews = crewsRepository.findById(crewId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));

        if (!invite.isComplete()) {
            throw new TickitaException(ErrorCode.INVITATION_INVALID);
        }
        if(crewListRepository.findByAccountAndCrews(invite, crews).isPresent()){
            throw new TickitaException(ErrorCode.INVITATION_ALREADY_SENT);
        }

        CrewList crewList = new CrewList(crews, invite, crews.getLabelColor(), CrewRole.MEMBER, CrewAccept.WAIT);
        crewListRepository.save(crewList);

        Notification savedNotification = notificationRepository.save(new Notification(NotificationType.INVITE));

        crewNotificationRepository.save(new CrewNotification(savedNotification, crewList));

        sendMail(email, crews.getCrewName(), invite.getNickName());
        return "그룹 초대 성공";
    }
}
