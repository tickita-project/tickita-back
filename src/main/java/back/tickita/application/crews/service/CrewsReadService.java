package back.tickita.application.crews.service;


import back.tickita.application.crews.dto.response.CrewAllInfo;
import back.tickita.application.crews.dto.response.CrewAllResponse;
import back.tickita.application.crews.dto.response.CrewInfoResponse;
import back.tickita.application.crews.dto.response.CrewMemberInfoResponse;
import back.tickita.domain.account.entity.Account;
import back.tickita.domain.account.repository.AccountRepository;
import back.tickita.domain.crews.entity.CrewList;
import back.tickita.domain.crews.entity.Crews;
import back.tickita.domain.crews.enums.CrewRole;
import back.tickita.domain.crews.repository.CrewListRepository;
import back.tickita.domain.crews.repository.CrewsRepository;
import back.tickita.exception.ErrorCode;
import back.tickita.exception.TickitaException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CrewsReadService {

    private final AccountRepository accountRepository;
    private final CrewListRepository crewListRepository;
    private final CrewsRepository crewsRepository;

    public CrewInfoResponse getCrewInfo(Long accountId, Long crewId) {
        CrewList crewList = crewListRepository.findByAccountIdAndCrewsId(accountId, crewId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        Crews crews = crewList.getCrews();

        List<CrewMemberInfoResponse> crewMemberInfos = crews.getCrewLists()
                .stream()
                .map(crewInfoList -> new CrewMemberInfoResponse(
                        crewInfoList.getCrewRole().name(), crewInfoList.getAccount().getId(), crewInfoList.getAccount().getNickName(), crewInfoList.getAccount().getEmail()))
                .sorted(Comparator.comparingInt(crewInfo -> CrewRole.valueOf(crewInfo.getRole()).getOrder()))
                .collect(Collectors.toList());

        return new CrewInfoResponse(crews.getCrewName(), crews.getLabelColor(), crews.getId(), crewMemberInfos);
    }

    public CrewAllResponse getCrewAll(Long accountId) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        List<CrewList> crewList = crewListRepository.findByAccountId(account.getId());

        List<CrewAllInfo> crewLists = crewList
                .stream()
                .map(crewAll -> new CrewAllInfo(
                        crewAll.getCrews().getId(), crewAll.getCrews().getCrewName(), crewAll.getCrews().getLabelColor()))
                .collect(Collectors.toList());

        return new CrewAllResponse(crewLists);
    }
}