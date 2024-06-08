package back.tickita.application.crews.service;

import back.tickita.application.account.dto.request.LoginUserInfo;
import back.tickita.application.crews.dto.request.CrewRequest;
import back.tickita.application.crews.dto.response.CrewCreateResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.enums.CrewAccept;
import back.tickita.domain.crews.enums.CrewRole;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.crews.repository.CrewsRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class CrewsWriteService {
    private final AccountRepository accountRepository;
    private final CrewsRepository crewsRepository;
    private final CrewListRepository crewListRepository;

    public CrewCreateResponse create(Long accountId, CrewRequest crewRequest) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        Crews crews = new Crews(crewRequest.getCrewName(), crewRequest.getLabelColor());
        crewsRepository.save(crews);

        CrewList createCrew = new CrewList(crews, account, CrewRole.OWNER, CrewAccept.ACCEPT);
        crewListRepository.save(createCrew);
        return new CrewCreateResponse(account.getId(), crewRequest.getCrewName(), crewRequest.getLabelColor());
    }
}
