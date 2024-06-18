package back.tickita.application.crews.service;

import back.tickita.application.crews.dto.request.CrewRequest;
import back.tickita.application.crews.dto.response.CrewAllInfo;
import back.tickita.application.crews.dto.response.CrewCreateResponse;
import back.tickita.application.crews.dto.response.CrewMemberInfoResponse;
import back.tickita.application.crews.dto.response.CrewMessageResponse;
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

        CrewList createCrew = new CrewList(crews, account, crewRequest.getLabelColor(), CrewRole.OWNER, CrewAccept.ACCEPT);
        crewListRepository.save(createCrew);
        return new CrewCreateResponse(account.getId(), crewRequest.getCrewName(), crewRequest.getLabelColor(), crews.getId());
    }

    public boolean isOwner(Long crewId, Long accountId) {
        return crewListRepository.existsByCrewsIdAndAccountIdAndCrewRole(crewId, accountId, CrewRole.OWNER);
    }

    public CrewAllInfo updateCrewInfo(Long crewId, Long accountId, CrewRequest crewInfoRequest) {
        Crews crew = crewsRepository.findById(crewId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        CrewList crewList = crewListRepository.findByAccountIdAndCrewsId(accountId, crewId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        if (isOwner(crewId, accountId)) {
            crew.setCrews(crewInfoRequest.getCrewName(), crewInfoRequest.getLabelColor());
            crewsRepository.save(crew);
            crewList.setIndividualColor(crewInfoRequest.getLabelColor());
            crewListRepository.save(crewList);
            return new CrewAllInfo(crewId, crew.getCrewName(), crew.getLabelColor());
        } else {
            crewList.setIndividualColor(crewInfoRequest.getLabelColor());
            crewListRepository.save(crewList);
            return new CrewAllInfo(crewId, crew.getCrewName(), crewList.getIndividualColor());
        }
    }

    public CrewMemberInfoResponse delegateOwner(Long crewId, Long accountId, Long memberId) {
        if (!isOwner(crewId, accountId)) {
            throw new TickitaException(ErrorCode.FORBIDDEN_ACCESS);
        }
        Crews crew = crewsRepository.findById(crewId).orElseThrow(() -> new TickitaException(ErrorCode.CREW_NOT_FOUND));
        Account member = accountRepository.findById(memberId).orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        // 기존의 오너를 일반 멤버로 변경
        CrewList currentOwner = crew.getCrewLists().stream()
                .filter(cl -> cl.getCrewRole() == CrewRole.OWNER)
                .findFirst()
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        currentOwner.setCrewRole(CrewRole.MEMBER);
        crewListRepository.save(currentOwner);

        // 새로운 오너를 추가
        CrewList newOwner = crew.getCrewLists().stream()
                .filter(cl -> cl.getAccount().getId().equals(member.getId()))
                .findFirst()
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));

        newOwner.setCrewRole(CrewRole.OWNER);
        crewListRepository.save(newOwner);

        return new CrewMemberInfoResponse(newOwner.getCrewRole().name(), newOwner.getAccount().getId(), newOwner.getAccount().getNickName(), newOwner.getAccount().getEmail());
    }

    public CrewMessageResponse leaveCrew(Long crewId, Long accountId) {
        if (isOwner(crewId, accountId)) {
            throw new TickitaException(ErrorCode.FORBIDDEN_ACCESS);
        }
        CrewList member = crewListRepository.findByAccountIdAndCrewsId(accountId, crewId)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        crewListRepository.delete(member);

        return new CrewMessageResponse(crewId, "그룹을 성공적으로 탈퇴하였습니다.");
    }

    public CrewMessageResponse removeMember(Long crewId, Long accountId, Long memberId) {
        if (!isOwner(crewId, accountId)) {
            throw new TickitaException(ErrorCode.FORBIDDEN_ACCESS);
        }
        CrewList member = crewListRepository.findByAccountIdAndCrewsId(memberId, crewId)
                .orElseThrow(() -> new TickitaException(ErrorCode.ACCOUNT_NOT_FOUND));
        crewListRepository.delete(member);

        return new CrewMessageResponse(crewId, "그룹의 멤버가 성공적으로 탈퇴되었습니다.");
    }

    public CrewMessageResponse deleteCrew(Long crewId, Long accountId) {
        if (!isOwner(crewId, accountId)) {
            throw new TickitaException(ErrorCode.FORBIDDEN_ACCESS);
        }
        crewsRepository.deleteById(crewId);

        return new CrewMessageResponse(crewId, "그룹이 성공적으로 삭제되었습니다.");
    }
}
